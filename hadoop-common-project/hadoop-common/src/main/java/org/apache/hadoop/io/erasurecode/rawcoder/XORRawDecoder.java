/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.erasurecode.rawcoder;

import java.nio.ByteBuffer;

import org.apache.hadoop.classification.InterfaceAudience;

/**
 * A raw decoder in XOR code scheme in pure Java, adapted from HDFS-RAID.
 *
 * XOR code is an important primitive code scheme in erasure coding and often
 * used in advanced codes, like HitchHiker and LRC, though itself is rarely
 * deployed independently.
 */
@InterfaceAudience.Private
public class XORRawDecoder extends AbstractRawErasureDecoder {

  public XORRawDecoder(int numDataUnits, int numParityUnits) {
    super(numDataUnits, numParityUnits);
  }

  @Override
  protected void doDecode(ByteBuffer[] inputs, int[] erasedIndexes,
                          ByteBuffer[] outputs) {
    ByteBuffer output = outputs[0];

    int erasedIdx = erasedIndexes[0];

    // Process the inputs.
    int iIdx, oIdx;
    for (int i = 0; i < inputs.length; i++) {
      // Skip the erased location.
      if (i == erasedIdx) {
        continue;
      }

      for (iIdx = inputs[i].position(), oIdx = output.position();
           iIdx < inputs[i].limit();
           iIdx++, oIdx++) {
        output.put(oIdx, (byte) (output.get(oIdx) ^ inputs[i].get(iIdx)));
      }
    }
  }

  @Override
  protected void doDecode(byte[][] inputs, int[] inputOffsets, int dataLen,
                          int[] erasedIndexes, byte[][] outputs,
                          int[] outputOffsets) {
    byte[] output = outputs[0];
    resetBuffer(output, outputOffsets[0], dataLen);

    int erasedIdx = erasedIndexes[0];

    // Process the inputs.
    int iIdx, oIdx;
    for (int i = 0; i < inputs.length; i++) {
      // Skip the erased location.
      if (i == erasedIdx) {
        continue;
      }

      for (iIdx = inputOffsets[i], oIdx = outputOffsets[0];
           iIdx < inputOffsets[i] + dataLen; iIdx++, oIdx++) {
        output[oIdx] ^= inputs[i][iIdx];
      }
    }
  }

}
