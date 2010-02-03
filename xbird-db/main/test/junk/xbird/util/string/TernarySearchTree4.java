package tuning.struct;

public class TernarySearchTree4
{
  final static int INITIAL_NODE = 1;
  final static int LOW_OFFSET = 1;
  final static int HIGH_OFFSET = 2;
  final static int EQUAL_OFFSET = 3;
  final static int VALUE_OFFSET = 4;
  final static int NODE_SIZE = 5;

  char[] buff = new char[5000];
  int[] nodes;
  Object[] objects;
  int nextNode = INITIAL_NODE;
  int nextObject = 0;

  public TernarySearchTree4()
  {
    this(500000);
  }

  public TernarySearchTree4(int size)
  {
    nodes = new int[NODE_SIZE*size];
    objects = new Object[size];
  }

  public Object get(String key)
  {
    key.getChars(0, key.length(), buff, 0);
    return search(buff, 0, key.length()-1);
  }

  public Object put(String key, Object value)
  {
    key.getChars(0, key.length(), buff, 0);
    if (nextNode == INITIAL_NODE)
    {
      nodes[INITIAL_NODE] = buff[0];
      nextNode+=NODE_SIZE;
    }
    return insert(buff, 0, key.length()-1, value);
  }

  public void release()
  {
    nodes = null;
    objects = null;
  }

  public Object search(char[] str, int strIdx, int strMaxIdx)
  {
    int p = INITIAL_NODE;
    int c;
    while (p != 0)
    {
      if ( (c = str[strIdx]) < nodes[p])
        p = nodes[p+LOW_OFFSET];
      else if (c == nodes[p])
      {
        if (strIdx == strMaxIdx)
	  return objects[nodes[p+VALUE_OFFSET]];
        else
        {
          strIdx++;
	  p = nodes[p+EQUAL_OFFSET];
        }
      }
      else
	p = nodes[p+HIGH_OFFSET];
    }
    return null;
  }

  public Object insert(char[] str, int strIdx, int strMaxIdx, Object o)
  {
    int p = INITIAL_NODE;
    int c = str[strIdx];
    Object old;
    for(;;)
    {
      if ( c < nodes[p])
      {
	if (nodes[p+LOW_OFFSET] == 0)
	{
	  nodes[p+LOW_OFFSET] = nextNode;
	  nodes[nextNode] = c;
	  nextNode+=NODE_SIZE;
	}
        p = nodes[p+LOW_OFFSET];
      }
      else if (c == nodes[p])
      {
	if (strIdx == strMaxIdx)
	{
	  if (nodes[p+VALUE_OFFSET] == 0)
	  {
	    nodes[p+VALUE_OFFSET] = nextObject;
	    nextObject++;
	  }
	  old = objects[nodes[p+VALUE_OFFSET]];
	  objects[nodes[p+VALUE_OFFSET]] = o;
	  return old;
	}
        else
        {
          strIdx++;
	  c=str[strIdx];
	  if (nodes[p+EQUAL_OFFSET] == 0)
	  {
	    nodes[p+EQUAL_OFFSET] = nextNode;
	    nodes[nextNode] = c;
	    nextNode+=NODE_SIZE;
	  }
          p = nodes[p+EQUAL_OFFSET];
        }
      }
      else
      {
	if (nodes[p+HIGH_OFFSET] == 0)
	{
	  nodes[p+HIGH_OFFSET] = nextNode;
	  nodes[nextNode] = c;
	  nextNode+=NODE_SIZE;
	}
        p = nodes[p+HIGH_OFFSET];
      }
    }
  }
}
