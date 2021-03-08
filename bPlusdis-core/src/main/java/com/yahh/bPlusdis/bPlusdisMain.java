package com.yahh.bPlusdis;

import com.yahh.bPlusdis.sds.SimDynamicStr;
import com.yahh.bPlusdis.tree.BPlusTree;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邹磊
 * @version 1.0
 * @description:
 * @date 2021/3/8 12:46
 */
public class bPlusdisMain {

    public static void main(String[] args) {
//        BPlusTree<Integer, String> myTree = new BPlusTree<Integer, String>(8);
//
//        int max = 1000000;
//        long start = System.currentTimeMillis();
//        for(int i = 0; i < max; i++) {
//            myTree.set(i, String.valueOf(i));
//        }
//        System.out.println("time cost with BPlusTree: " + (System.currentTimeMillis() - start));
//        System.out.println("Data has been inserted into tree");
//
//        System.out.println("height: " + myTree.height());
//
//        start = System.currentTimeMillis();
//        Map<Integer, String> hashMap = new HashMap<Integer, String>();
//        for (int i = 0; i < max; i++) {
//            hashMap.put(i, String.valueOf(i));
//        }
//        System.out.println("time cost with HashMap: " + (System.currentTimeMillis() - start));
//
//        for (int i = 0; i < max; i++) {
//            if (!String.valueOf(i).equals(myTree.get(i))) {
//                System.err.println("error for: " + i);
//            }
//        }
//
//        System.out.println("Success");

        BPlusTree<Integer, SimDynamicStr> myTree = new BPlusTree<Integer, SimDynamicStr>(8);

        int max = 100;
        long start = System.currentTimeMillis();
        for(int i = 0; i < max; i++) {
            SimDynamicStr sds = new SimDynamicStr();
            byte[] content = String.valueOf(i).getBytes();
            sds.setContent(content);
            sds.setLen(content.length);
            myTree.set(i,sds);
        }

        System.out.println("time cost with BPlusTree: " + (System.currentTimeMillis() - start));

        System.out.println("Success " + myTree.get(9));

        System.out.println("50: " + new String(String.valueOf(9).getBytes()));


    }

}
