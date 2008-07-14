TEST_FILE="./test-reports_wrapped.xml"
ORIG_FILE="./test-reports.xml"
TARGET_FILE="./test-reports_wf.xml"

lcp="../../../lib/optional/saxon8.jar:../../../lib/optional/saxon8-dom.jar"

rm -rf $TEST_FILE $TARGET_FILE
touch $TEST_FILE
echo "<root>" >> $TEST_FILE
cat $ORIG_FILE >> $TEST_FILE
echo "</root>" >> $TEST_FILE

echo "java -Xmx512m -cp $lcp net.sf.saxon.Query -o $TARGET_FILE transform.xq"
java -Xmx512m -cp $lcp net.sf.saxon.Query -o $TARGET_FILE transform.xq
