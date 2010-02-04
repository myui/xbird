#!/bin/sh

xmark_dir="C:\Software\xmark"
xbird_home="D:\workspace\xbird"
out_dir="$xmark_dir\benchmark\xbird\sf100_idx"

VMARGS="-Djava.library.path=$xbird_home/lib/native -Xms1024m -Xmx1024m -da"
#VMARGS="-Djava.library.path=$xbird_home/lib/native -Xms1024m -Xmx1024m -da -Dxbird.paging.rf_idx=8"
#VMARGS="-Djava.library.path=$xbird_home/lib/native -Xms96m -Xmx96m -da -Dxbird.page_caches=12 -Dxbird.cchunk_caches=12"
#VMARGS="-Djava.library.path=$xbird_home/lib/native -Xms512m -Xmx1024m -da -Dxbird.disable_binfilter"
#VMARGS="-Djava.library.path=$xbird_home/lib/native -Xms1024m -Xmx1024m -da -Dxbird.disable_index_access"

cp="$xbird_home\lib\optional\args4j-2.0.4.jar;$xbird_home\lib\optional\asm-3.0.jar;$xbird_home\lib\commons-logging-1.0.4.jar"

rm -rf $out_dir
mkdir $out_dir

for i in $(seq 1 20)
do
	cp "$xmark_dir\q$i.xq" "$xmark_dir\q$i.xq.run"
	perl -pi -e 's|doc\("auction.xml"\)|collection\("/xmark100/xmark100.xml"\)|' "$xmark_dir/q$i.xq.run"
done

cd ..
maven java:compile java:jar-resources 
sleep 3

for i in $(seq 1 20)
do
  echo "-- q$i.xq.run --" | tee -a "$out_dir\out.txt"
  java $VMARGS -cp "$cp;$xbird_home\target\classes" org.metabrick.xbird.client.InteractiveShell -o "$out_dir\res$i.txt" -q "$xmark_dir\q$i.xq.run" -encoding "UTF-8" 2>&1 | tee -a "$out_dir\out.txt"
  sleep 3
done

rm -rf "$out_dir\*.run"

cat "$out_dir\out.txt" | grep -iE "Compiling|Execution time:|ms|--|error|exception|fail" > "$out_dir\out_summary.txt"
