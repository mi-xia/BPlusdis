package com.yahh.bPlusdis.tree;

/**
 * @author 邹磊
 * @version 1.0
 * @description:
 * @date 2021/3/2 2:25
 */
public class BPlusTree <T extends Comparable<T>,V> {


    // m阶B+树
    private int factor;

    private static final int DEFAULT_FACTOR = 5;

    // 非叶子节点的最小子节点数（m/2）向上取整
    private int MIN_CHILDREN_FOR_INTERNAL;
    // 非叶子节点的最大子节点数 m
    private int MAX_CHILDREN_FOR_INTERNAL;
    // 叶子节点的最小节点数（m/2）向上取整
    private int MIN_FOR_LEAF;
    // 叶子结点的最大节点数 m-1 ？？？？？？
    private int MAX_FOR_LEAF;

    private Node<T, V> root = null;

    public BPlusTree() {
        this(DEFAULT_FACTOR);
    }

    public BPlusTree(int factor) {
        this.factor = factor;

        this.MIN_CHILDREN_FOR_INTERNAL = Double.valueOf(Math.ceil(1.0 * this.factor / 2)).intValue();
        this.MAX_CHILDREN_FOR_INTERNAL = this.factor;
        this.MIN_FOR_LEAF = Double.valueOf(Math.floor(1.0 * this.factor / 2)).intValue();
        this.MAX_FOR_LEAF = this.factor - 1;

        this.root = new LeafNode<T, V>();
    }

    public void set(T key, V value) {
        if (key == null) {
            throw new NullPointerException("must not be null for key.");
        }
        Node node = this.root.insert(key, value);

        if (node != null) {
            this.root = node;
        }
    }

    public V get(T key) {
        return this.root.get(key);
    }

    public int height() {
        int height = 1;
        Node node = this.root;
        while( !(node instanceof LeafNode)) {
            height++;
            node = ((InternalNode)node).pointers[0];
        }

        return height;
    }

    /**
     * 节点抽象定义
     *
     * @param <T>
     * @param <V>
     */
    abstract class Node<T extends Comparable<T>, V> {

        protected Node<T, V> parent;

        protected Object[] keys;

        protected int size;


        /**
         * if new parent node is created when insert the key-value, the created parent node is returned,
         * in other case, this method return null.
         *
         * @param key
         * @param value
         * @return
         */
        abstract Node<T, V> insert(T key, V value);

        abstract V get(T key);
    }

    /**
     *
     * 非叶子结点
     * @param <T>
     * @param <V>
     */
    class InternalNode<T extends Comparable<T>, V> extends Node<T, V> {
        private Node<T, V>[] pointers;

        public InternalNode() {
            this.size = 0;
            this.pointers = new Node[MAX_CHILDREN_FOR_INTERNAL + 1];
            this.keys = new Object[MAX_CHILDREN_FOR_INTERNAL];
        }

        @Override
        public Node<T, V> insert(T key, V value) {
            // 插入时首先顺序遍历所有的key 找到正确的位置
            int i = 0;
            for (; i < this.size; i++) {
                if ( key.compareTo( (T)this.keys[i] ) < 0 ) {
                    break;
                }
            }

            return this.pointers[i].insert(key, value);
        }

        @Override
        public V get(T key) {
            int i = 0;
            for (; i < this.size; i++) {
                if ( key.compareTo( (T)this.keys[i] ) < 0) {
                    break;
                }
            }

            return this.pointers[i].get(key);
        }

        /**
         *
         * @param key
         * @param leftChild
         * @param rightChild
         * @return
         */
        private Node<T, V> insert(T key, Node<T, V> leftChild, Node<T, V> rightChild){
            // size==0说明是一个初始化的节点
            if (this.size == 0) {
                this.size++;
                this.pointers[0] = leftChild;
                this.pointers[1] = rightChild;
                this.keys[0] = key;

                leftChild.parent = this;
                rightChild.parent = this;

                return this;
            }

            Object[] newKeys = new Object[MAX_CHILDREN_FOR_INTERNAL + 1];
            Node[] newPointers = new Node[MAX_CHILDREN_FOR_INTERNAL + 2];

            int i = 0;
            for(; i < this.size; i++) {
                T curKey = (T)this.keys[i];
                if (curKey.compareTo(key) > 0) {
                    break;
                }
            }

            System.arraycopy(this.keys, 0, newKeys, 0, i);
            newKeys[i] = key;
            System.arraycopy(this.keys, i, newKeys, i + 1, this.size - i);

            System.arraycopy(this.pointers, 0, newPointers, 0, i + 1);
            newPointers[i + 1] = rightChild;
            System.arraycopy(this.pointers, i + 1, newPointers, i + 2, this.size - i);

            this.size++;
            if(this.size <= MAX_CHILDREN_FOR_INTERNAL) {
                System.arraycopy(newKeys, 0, this.keys, 0, this.size);
                System.arraycopy(newPointers, 0, this.pointers, 0, this.size + 1);
                return null;
            }

            int m = (this.size / 2);

            // split the internal node
            InternalNode<T, V> newNode = new InternalNode<T, V>();

            newNode.size = this.size - m - 1;
            System.arraycopy(newKeys, m + 1, newNode.keys, 0, this.size - m - 1);
            System.arraycopy(newPointers, m + 1, newNode.pointers, 0, this.size - m);

            // reset the children's parent to the new node.
            for(int j = 0; j <= newNode.size; j++) {
                newNode.pointers[j].parent = newNode;
            }

            this.size = m;
            this.keys = new Object[MAX_CHILDREN_FOR_INTERNAL];
            this.pointers = new Node[MAX_CHILDREN_FOR_INTERNAL];
            System.arraycopy(newKeys, 0, this.keys, 0, m);
            System.arraycopy(newPointers, 0, this.pointers, 0, m + 1);

            if (this.parent == null) {
                this.parent = new InternalNode<T, V>();
            }
            newNode.parent = this.parent;

            return ((InternalNode<T, V>)this.parent).insert((T)newKeys[m], this, newNode);
        }
    }

    /**
     *
     * 叶子节点
     * @param <T>
     * @param <V>
     */
    class LeafNode<T extends Comparable<T>, V> extends Node<T, V> {
        private Object[] values;

        public LeafNode() {
            this.size = 0;
            this.keys = new Object[MAX_FOR_LEAF];
            this.values = new Object[MAX_FOR_LEAF];
            this.parent = null;
        }

        @Override
        public Node<T, V> insert(T key, V value) {
            Object[] newKeys = new Object[MAX_FOR_LEAF + 1];
            Object[] newValues = new Object[MAX_FOR_LEAF + 1];

            int i = 0;
            // 循环遍历该节点所有的key，与输入的key做比较
            for (; i < this.size; i++) {
                T curKey = (T) this.keys[i];

                // 如果相同 则直接覆盖value
                if (curKey.compareTo(key) == 0) {
                    this.values[i] = value;
                    return null;
                }
                // 如果该key > 输入key 说明输入key应该小于keys[i],即位于 i-1 到 i 之间
                if (curKey.compareTo(key) > 0) {
                    break;
                }
            }

            // 把 k v 塞进节点中，同时size++
            System.arraycopy(this.keys, 0, newKeys, 0, i);
            newKeys[i] = key;
            System.arraycopy(this.keys, i, newKeys, i + 1, this.size - i);

            System.arraycopy(this.values, 0, newValues, 0, i);
            newValues[i] = value;
            System.arraycopy(this.values, i, newValues, i + 1, this.size - i);

            this.size++;

            // 当前节点key数<=叶子结点最大key数，直接返回
            if (this.size <= MAX_FOR_LEAF){
                System.arraycopy(newKeys, 0, this.keys, 0, this.size);
                System.arraycopy(newValues, 0, this.values, 0, this.size);
                return null;
            }


            // 否则需要切分该节点
            int m = this.size / 2;

            this.keys = new Object[MAX_FOR_LEAF];
            this.values = new Object[MAX_FOR_LEAF];
            System.arraycopy(newKeys, 0, this.keys, 0, m);
            System.arraycopy(newValues, 0, this.values, 0, m);

            // 创建一个新node 将切分的右半部分塞进node
            LeafNode<T, V> newNode = new LeafNode<T, V>();
            newNode.size = this.size - m;
            System.arraycopy(newKeys, m, newNode.keys, 0, newNode.size);
            System.arraycopy(newValues, m, newNode.values, 0, newNode.size);

            this.size = m;
            // 创建一个父节点，并将该node 和创建的新node指向父节点
            if (this.parent == null) {
                this.parent = new InternalNode<T, V>();
            }
            newNode.parent = this.parent;

            //最后将新node的第一个值塞进父node中
            return ((InternalNode<T, V>)this.parent).insert((T)newNode.keys[0], this, newNode);
        }

        @Override
        public V get(T key) {
            // two branch search
            if (this.size == 0) {
                return null;
            }

            int start = 0;
            int end = this.size;

            int middle = (start + end) / 2;

            while (start < end) {
                T middleKey = (T)this.keys[middle];
                if (key.compareTo(middleKey) == 0){
                    break;
                }

                if (key.compareTo(middleKey) < 0) {
                    end = middle;
                } else {
                    start = middle;
                }

                middle = (start + end) / 2;
            }

            T middleKey = (T) this.keys[middle];

            return middleKey.compareTo(key) == 0 ? (V) this.values[middle] : null;
        }
    }



}
