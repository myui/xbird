package scenario.storage;

import java.io.*;

import xbird.storage.index.*;
import xbird.storage.indexer.*;
import xbird.util.datetime.StopWatch;

public class BTreeScenarioTest {

    private final String btreePath;

    private BTreeScenarioTest(String btreePath) {
        this.btreePath = btreePath;
    }

    public static void main(String[] args) {
        final String bfilePath;
        if(args.length < 1) {
            final File file;
            try {
                file = File.createTempFile("test", ".btree");
                file.delete();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            bfilePath = file.getAbsolutePath();
        } else {
            bfilePath = args[0];
        }
        System.err.println("Configured to use '" + bfilePath + "' as the BFile\n");
        BTreeScenarioTest test = new BTreeScenarioTest(bfilePath);
        printUsage();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.err.print("> ");
            final String line;
            try {
                line = input.readLine();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            String[] commands = line.split(" ");
            test.doTask(commands);
        }
    }

    private void doTask(String[] args) {
        try {
            if(args[0].equals("create")) {
                BTree f = new BTree(new File(btreePath));
                if(f.exists()) {
                    System.err.println("BFile already exists: " + btreePath);
                    return;
                }
                f.create(false);
                f.flush(true, true);
                System.err.println("Created BFile: " + btreePath);
                f.close();
            } else if(args[0].equals("import")) {
                StopWatch sw = new StopWatch("elasped time");
                BTree f = new BTree(new File(btreePath));
                f.open();
                FileInputStream fis = new FileInputStream(args[1]);
                DataInputStream dis = new DataInputStream(fis);
                int i = 0;
                while(dis.available() > 0) {
                    String val = dis.readLine().trim();
                    assert (val.length() > 0);
                    f.addValue(new Value(val.getBytes("UTF-8")), i++);
                }
                fis.close();
                f.flush();
                f.close();
                System.err.println(sw);
            } else if(args[0].equals("export")) {
                StopWatch sw = new StopWatch("elasped time");
                BTree f = new BTree(new File(btreePath));
                f.open();
                FileOutputStream fos = new FileOutputStream(args[1]);
                final PrintStream ps = new PrintStream(fos);
                f.search(new BasicIndexQuery.IndexConditionANY(), new CallbackHandler() {
                    public boolean indexInfo(Value value, long pointer) {
                        ps.println(value);
                        return true;
                    }
                    public boolean indexInfo(Value key, byte[] value) {
                        throw new IllegalStateException();
                    }
                });
                fos.close();
                f.close();
                System.err.println(sw);
            } else if(args[0].equals("find")) {
                StopWatch sw = new StopWatch("elasped time");
                BTree f = new BTree(new File(btreePath));
                f.open();
                long ptr = f.findValue(new Value(args[1]));
                System.out.println(ptr);
                f.close();
                System.err.println(sw);
            } else if(args[0].equals("count")) {
                StopWatch sw = new StopWatch("elasped time");
                BTreeIndexer indexer = new BTreeIndexer(new File(btreePath));
                IndexMatch matched = indexer.find(new BasicIndexQuery.IndexConditionEQ(new Value(args[1])));
                System.out.println(matched.countMatched());
                System.err.println(sw);
            } else if(args[0].equals("add")) {
                StopWatch sw = new StopWatch("elasped time");
                BTree f = new BTree(new File(btreePath));
                f.open();
                long pointer = Long.parseLong(args[2]);
                System.err.println("add key: " + args[1] + ", value: " + pointer);
                long old = f.addValue(new Value(args[1]), pointer);
                if(old != -1L) {
                    System.err.println("old: " + old);
                }
                f.flush(true, true);
                f.close();
                System.err.println(sw);
            } else if(args[0].equals("delete")) {
                BTree f = new BTree(new File(btreePath));
                if(f.drop()) {
                    System.err.println("deleted BFile: " + btreePath);
                } else {
                    System.err.println("failed to delete BFile: " + btreePath);
                }
                f.close();
            } else if(args[0].equals("exit")) {
                File file = new File(btreePath);
                if(file.exists()) {
                    file.delete();
                }
                System.err.println("bye!");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("usage:");
        System.out.println("   create ");
        System.out.println("      Create a new B+Tree file");
        System.out.println();
        System.out.println("   import <import file>");
        System.out.println("      Import text line by line into B+Tree");
        System.out.println();
        System.out.println("   export <export file>");
        System.out.println("      Export values line by line from B+Tree");
        System.out.println();
        System.out.println("   find <value>");
        System.out.println("      Find value in the B+Tree");
        System.out.println();
        System.out.println("   count <value>");
        System.out.println("      Count values in the B+Tree");
        System.out.println();
        System.out.println("   add <key> <value>");
        System.out.println("      Add value to the B+Tree");
        System.out.println();
        System.out.flush();
    }
}