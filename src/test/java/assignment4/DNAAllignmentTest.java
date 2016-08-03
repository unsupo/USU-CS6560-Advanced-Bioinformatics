package assignment4;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by jarndt on 8/3/16.
 */
public class DNAAllignmentTest {
    static DNAAllignment align;

    @BeforeClass
    public static void init(){
        align = new DNAAllignment();
    }

    @Test
    public static void test1SingleCharacter(){
        assertEquals(align.getScoreDP("a","b"),1);
    }

    @Test
    public static void test2TwoCharacters(){
        assertEquals(align.getScoreDP("aa","bb"),2);
    }
    @Test
    public static void test3TwoCharacters(){
        assertEquals(align.getScoreDP("ab","bb"),1);
    }

    @Test
    public static void test4TwelveCharacters(){
        String rand = getRandomString(12), test = rand.substring(0,5);
        assertEquals(align.getScoreDP(rand,test),7);
    }
    @Test
    public static void test5TwelveCharacters(){
        String rand = getRandomString(12), test = rand.substring(2,7);
        assertEquals(align.getScoreDP(rand,test),8);
    }

    @Test
    public static void test6ThrityCharacters(){
        String rand = getRandomString(30), test = rand.substring(13,26);
        assertEquals(align.getScoreDP(rand,test),18);
    }


    private static String getRandomString(int length){
        Random r = new Random();
        String s = "";
        for (int i = 0; i<length; i++)
            s+=(char)(r.nextInt('z'-'a')+'a');
        return s;
    }
}
