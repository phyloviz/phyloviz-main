///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.phyloviz.nj.algorithm;
//
//import net.phyloviz.nj.algorithm.studier_keppler.NJDistanceStudierKeppler;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import net.phyloviz.algo.AbstractDistance;
//import net.phyloviz.alseq.AlignedSequence;
//import net.phyloviz.core.data.TypingData;
//import net.phyloviz.mlst.MLSTypingFactory;
//import net.phyloviz.nj.json.JsonValidator;
//import net.phyloviz.nj.tree.NJLeafNode;
//import org.junit.Test;
//import org.junit.Before;
//
///**
// *
// * @author Adriano
// */
//public class NJTest {
//    
//    AbstractDistance<NJLeafNode> ad;
//    TypingData<AlignedSequence> td;
//    int iterations = 1;
//
//    @Before
//    public void setUp() {
//        ad = new NJDistanceStudierKeppler();
//        MLSTypingFactory factory = new MLSTypingFactory();
//        try{
//            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Adriano\\Desktop\\PS\\DadosPhyloviz\\5000strep.typing.csv"));
//            td = (TypingData<AlignedSequence>) factory.loadData(br);
//        } catch(Exception e){
//            throw new RuntimeException(e);
//        }
//    }
//    @Test
//    public void test1() {
//        
//        //new JsonValidator().validate(null, null);
//        
////        System.out.println("NJ -> ArrayList de arrays");
////
////        long start = System.currentTimeMillis();
////        NJ instance = new NJWikipedia(td, ad, null);
////                
////        NJRoot root = instance.generateTree();
////
////        long end = System.currentTimeMillis();
////        System.out.println(end - start);
//        
////        NJToJSON js = new NJToJSON(root);
////        System.out.println(js.saveToJSON());
//        
//    }
//}
