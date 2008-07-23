/*
 * @(#)$Id$
 *
 * Copyright (c) 2005-2008 Makoto YUI and Project XBird
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */

#include "xbird_util_Primitives.h"

/*
 * Class:     xbird_util_Primitives
 * Method:    toIntsJni
 * Signature: ([BII)[I
 */
JNIEXPORT jintArray JNICALL Java_xbird_util_Primitives_toIntsJni
  (JNIEnv *env, jclass caller, jbyteArray jbytes, jint offset, jint length)
{
	jboolean isCopy;
	int arylen = length >> 2;
	jintArray jints = env->NewIntArray(arylen);
	jint* pi = env->GetIntArrayElements(jints, NULL);
	jbyte* pb = env->GetByteArrayElements(jbytes, &isCopy);
	for(int i = 0, bi = offset; i < arylen; i++, bi += 4) {
		pi[i] = (pb[bi] << 24) + ((pb[bi + 1] & 0xFF) << 16) + ((pb[bi + 2] & 0xFF) << 8)
                    + (pb[bi + 3] & 0xFF);
	}
	env->ReleaseByteArrayElements(jbytes, pb, JNI_ABORT);
	if(isCopy) {
		env->ReleaseIntArrayElements(jints, pi, 0);
	} else {
		env->ReleaseIntArrayElements(jints, pi, JNI_ABORT);
	}
	return jints;	
}

/*
 * Class:     xbird_util_Primitives
 * Method:    toLongsJni
 * Signature: ([BII)[J
 */
JNIEXPORT jlongArray JNICALL Java_xbird_util_Primitives_toLongsJni
  (JNIEnv* env, jclass caller, jbyteArray jbytes, jint offset, jint length)
{
	jboolean isCopy;
	int arylen = length >> 3;
	jlongArray jlongs = env->NewLongArray(arylen);
	jlong* pl = env->GetLongArrayElements(jlongs, NULL);
	jbyte* pb = env->GetByteArrayElements(jbytes, &isCopy);
    for(int i = 0, bi = offset; i < arylen; i++, bi += 8) {
        pl[i] = ((jlong) pb[bi + 7] & 0xFFL) + (((jlong) pb[bi + 6] & 0xFFL) << 8) + (((jlong) pb[bi + 5] & 0xFFL) << 16)
                + (((jlong) pb[bi + 4] & 0xFFL) << 24) + (((jlong) pb[bi + 3] & 0xFFL) << 32)
                + (((jlong) pb[bi + 2] & 0xFFL) << 40) + (((jlong) pb[bi + 1] & 0xFFL) << 48)
                + (((jlong) pb[bi]) << 56);
    }
	env->ReleaseByteArrayElements(jbytes, pb, JNI_ABORT);
	if(isCopy) {
		env->ReleaseLongArrayElements(jlongs, pl, 0);
	} else {
		env->ReleaseLongArrayElements(jlongs, pl, JNI_ABORT);
	}
	return jlongs;
}
