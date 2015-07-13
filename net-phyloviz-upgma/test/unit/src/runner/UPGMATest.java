/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import net.phyloviz.alseq.AlignedSequence;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.mlst.MLSTypingFactory;
import net.phyloviz.upgma.distance.HammingDistance;
import net.phyloviz.upgma.distance.HierarchicalClusteringDistance;
import net.phyloviz.upgma.HierarchicalClusteringMethod;
import net.phyloviz.upgma.algorithm.UPGMA;
import net.phyloviz.upgma.algorithm.upgma.UPGMAMethod;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgma.ui.OutputPanel;
import org.junit.Test;

/**
 *
 * @author Adriano
 */
public class UPGMATest {
    
    static boolean[] IDS;
    static int N;
    static final int CYCLES = 1;
    
    @Test
    public void UPGMATimeTest() throws IOException {
              
        int size = 50;
        run(size);
        
        size = 100;
        run(size);
        
        size = 500;
        run(size);
        
        size = 1000;
        run(size);
        
        size = 5000;
        run(size);
//        
//        size = 6000;
//        run(size);
//        
//        size = 7000;
//        run(size);
//        
//        size = 8000;
//        run(size);
//        
//        size = 9000;
//        run(size);
//        
//        size = 10000;
//        run(size);
    }
    private void run(int size) throws IOException{
        
        MLSTypingFactory factory = new MLSTypingFactory(); 
        //BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Marta Nascimento\\Documents\\8ºSemestre-1415v\\PFC\\DadosPhyloviz\\"+size+"strep.typing.csv"));
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Adriano\\Desktop\\PS\\DadosPhyloviz\\"+size+"strep.typing.csv"));
        //BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Marta Nascimento\\Documents\\8ºSemestre-1415v\\PFC\\DadosPhyloviz\\spneumoniaeClean10.txt"));
        TypingData<AlignedSequence> td = (TypingData<AlignedSequence>) factory.loadData(br);            
        HierarchicalClusteringDistance ad = new HammingDistance();
        HierarchicalClusteringMethod cm = new UPGMAMethod();
       
        long init = 0, end = 0;
        init = System.currentTimeMillis();
        
        UPGMA algorithm = new UPGMA(td, ad, cm, new OutputPanel("upgma-test"));
                

        //Act
        UPGMARoot root = algorithm.execute();
        
        end = (System.currentTimeMillis() - init);
        System.out.println("UPGMA#"+size+": " + (float)end + " ms");
        
//        UPGMAToJSON js = new UPGMAToJSON(root);
//        System.out.println(js.saveToJSON());
    }
}

