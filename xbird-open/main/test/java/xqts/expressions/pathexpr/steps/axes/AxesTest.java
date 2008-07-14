/*
 * @(#)$Id: XQTSTest.template 946 2006-09-14 03:31:56Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
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
package xqts.expressions.pathexpr.steps.axes;

import junit.framework.TestCase;
import xqts.XQTSTestBase;

public class AxesTest extends TestCase {

    private static final String TEST_PATH = "((//ns:test-group[count(child::ns:test-group)=0])[10]//ns:test-case)";
    private static final String TARGET_XQTS_VERSION = "1.0.2";
    
    private final XQTSTestBase xqts;

    public AxesTest() {
    	super(AxesTest.class.getName());
        this.xqts = new XQTSTestBase(AxesTest.class.getName(), TARGET_XQTS_VERSION);
    }
    @org.junit.Test(timeout=300000)
    public void testAxes001_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[1]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes001_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[2]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes001_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[3]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes002_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[4]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes002_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[5]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes002_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[6]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes002_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[7]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes003_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[8]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes003_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[9]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes003_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[10]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes003_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[11]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes004_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[12]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes004_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[13]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes004_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[14]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes005_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[15]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes005_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[16]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes005_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[17]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes005_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[18]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes006_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[19]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes006_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[20]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes006_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[21]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes006_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[22]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes007_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[23]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes007_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[24]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes007_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[25]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes008_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[26]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes008_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[27]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes008_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[28]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes009_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[29]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes009_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[30]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes009_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[31]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes010_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[32]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes010_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[33]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes010_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[34]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes011_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[35]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes011_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[36]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes011_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[37]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes012_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[38]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes013_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[39]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes014_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[40]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes015_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[41]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes016_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[42]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes017_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[43]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes018_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[44]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes019_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[45]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes020_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[46]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes021_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[47]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes023_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[48]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes027_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[49]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes030_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[50]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes030_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[51]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes031_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[52]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes031_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[53]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes031_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[54]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes031_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[55]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes032_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[56]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes032_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[57]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes032_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[58]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes032_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[59]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes033_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[60]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes033_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[61]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes033_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[62]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes033_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[63]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes034_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[64]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes034_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[65]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes035_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[66]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes035_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[67]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes035_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[68]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes035_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[69]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes036_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[70]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes036_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[71]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes037_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[72]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes037_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[73]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes041_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[74]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes043_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[75]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes043_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[76]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes044_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[77]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes044_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[78]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes045_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[79]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes045_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[80]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes046_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[81]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes046_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[82]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes047_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[83]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes047_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[84]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes048_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[85]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes048_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[86]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes049_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[87]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes049_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[88]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes055_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[89]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes056_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[90]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes056_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[91]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes056_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[92]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes057_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[93]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes057_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[94]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes057_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[95]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes057_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[96]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes058_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[97]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes058_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[98]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes058_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[99]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes059_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[100]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes059_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[101]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes060_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[102]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes060_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[103]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes060_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[104]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes060_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[105]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes061_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[106]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes061_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[107]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes062_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[108]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes062_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[109]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes063_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[110]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes063_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[111]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes063_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[112]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes063_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[113]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes064_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[114]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes064_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[115]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes064_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[116]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes065_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[117]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes065_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[118]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes066_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[119]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes066_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[120]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes066_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[121]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes066_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[122]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes067_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[123]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes067_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[124]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes067_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[125]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes068_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[126]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes068_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[127]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes068_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[128]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes069_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[129]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes069_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[130]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes069_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[131]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes070_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[132]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes070_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[133]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes070_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[134]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes071_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[135]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes071_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[136]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes071_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[137]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes072_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[138]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes072_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[139]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes073_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[140]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes073_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[141]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes074_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[142]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes074_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[143]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes074_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[144]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes074_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[145]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes075_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[146]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes075_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[147]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes075_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[148]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes075_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[149]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes076_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[150]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes076_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[151]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes076_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[152]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes076_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[153]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes077_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[154]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes077_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[155]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes077_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[156]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes078_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[157]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes078_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[158]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes078_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[159]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes078_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[160]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes079_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[161]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes079_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[162]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes079_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[163]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes079_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[164]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes080_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[165]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes080_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[166]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes080_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[167]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes081_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[168]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes081_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[169]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes081_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[170]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes081_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[171]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes082_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[172]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes082_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[173]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes082_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[174]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes083_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[175]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes083_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[176]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes083_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[177]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes084_1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[178]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes084_2() throws Exception {
        xqts.invokeTest(TEST_PATH + "[179]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes084_3() throws Exception {
        xqts.invokeTest(TEST_PATH + "[180]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes084_4() throws Exception {
        xqts.invokeTest(TEST_PATH + "[181]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes085() throws Exception {
        xqts.invokeTest(TEST_PATH + "[182]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes086() throws Exception {
        xqts.invokeTest(TEST_PATH + "[183]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes087() throws Exception {
        xqts.invokeTest(TEST_PATH + "[184]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxes088() throws Exception {
        xqts.invokeTest(TEST_PATH + "[185]");
    }

    @org.junit.Test(timeout=300000)
    public void testAxisErr1() throws Exception {
        xqts.invokeTest(TEST_PATH + "[186]");
    }

}