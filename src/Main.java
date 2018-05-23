import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

import java.util.Map.Entry;
import java.util.TreeMap;

class Node implements Serializable
{                         
    ArrayList<String> V;
    double[] prior;//先验概率
    double[][] condprob;//条件概率
    Node()
    {
        V=new ArrayList<String>();
    }
}
public class Main
//朴素贝叶斯分类
{
	
	private static boolean isChineseSymbol(String text) {
		if(text.equals("。")) return true;
		if(text.equals("，")) return true;
		if(text.equals("？")) return true;
		if(text.equals("！")) return true;
		if(text.equals("“")) return true;
		if(text.equals("”")) return true;
		if(text.equals("‘")) return true;
		if(text.equals("’")) return true;
		if(text.equals("、")) return true;
		if(text.equals("：")) return true;
		if(text.equals("；")) return true;
		return false;
	}

	  public static void readfile(TreeMap<String,String> Set,String file_name) throws IOException
	  {
		  BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file_name),"utf-8"));
			String string = new String();
			String head=new String();
			String doc_class=new String();
			
	        while (true) 
	        {  
	            string = reader.readLine();  
	            if(string!=null)  
	            {
	            	if(string.length()>2)
	            	{
	            		head=string.substring(0,2);
	            		if(head.equals("#="))//类别
	            		{
	            			if(string.charAt(3)=='1')
	            			{//非理工类学术讲座
	            				doc_class="1";
	            			}
	            			if(string.charAt(3)=='0')
	            			{//理工类学术讲座
	            				doc_class="0";
	            			}
	            		}
	            		if(head.equals("#%"))
	            		{
	            			string=string.substring(3);
	            			Set.put(string,doc_class);
	            		}
	            	}
	            }
	            else  
	                break;  
	        }
	  }
	
      public static Node train(ArrayList<String> classSet, TreeMap<String,String> trainingSet)
      {//通过训练集来确定各个类的先验概率和条件概率,参数为类集合,训练集
    	  System.out.println("文章切词中....");
          ArrayList<String> V=new ArrayList<String>();
          for(int i=0;i<trainingSet.size();i++)
          {//将训练集中的文章加入到文档集合中
              for(Entry<String, String> entry:trainingSet.entrySet())
              {
                 String str=entry.getKey();//获取每篇文章的正文
                 //System.out.println(str);
                 str=str.toLowerCase();//标准化正文内容
                 JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
               
     			 List<SegToken> segTokens = segmenter.process(str, SegMode.SEARCH);
     			 for(SegToken segToken : segTokens) 
     			 {
     				if(!V.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) {
     					//将切好的词加入词项列表
     					V.add(segToken.word);
     				}
     			}

                 
                 /*JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
                 for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
                 {
                	 String word=segmenter.process(str, SegMode.INDEX).get(j).word;
                	 if(!V.contains(word)&&!word.equals(" "))
                     {//过滤掉重复和空格
                		 V.add(word);
                		// System.out.println(word);
                     }
           	     }*/
              }
          }
          
          double N=trainingSet.size();//文档总数
          double[] prior=new double[classSet.size()];//先验概率
          double[][] condprob=new double[V.size()][classSet.size()];//条件概率
          int i=0;
          for(String c:classSet)
          {//dasdas
              //CountDocsInClass,计算Nc，Nc为训练集合中c 类所包含的文档数目
              double Nc=0;//该类的文档数目
              String text="";//该类的文档
              System.out.println("计算先验频率中....");
              for(Entry<String, String> entry:trainingSet.entrySet())
              {//遍历文章集合
                  if(entry.getValue().equals(c))
                  {//计算每一类中的文章总数
                      Nc++;
                      text+=entry.getKey().toLowerCase();
                      //将类别c下的文档连接成一个大字符串,以空格隔开
                      text+=" ";
                  } 
              }
              prior[i]=Nc/N;//计算先验概率
       
              double[] Tct=new double[V.size()];//每个单词出现的次数      
              int j=0;
              System.out.println("计算条件概率切词中....");
              for(String t:V)
              {
                  double count=0;
                  JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
                  
                  List<SegToken> segTokens = segmenter.process(text, SegMode.SEARCH);
      			  for(SegToken segToken : segTokens) 
      			  {
      				  if(t.equals(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) {
      					  //将切好的词加入词项列表
      					  count++;
      				  }
      			  }
                  
                  /*for(int x=0;x<segmenter.process(text, SegMode.SEARCH).size();x++)
                  {
                 	 String word=segmenter.process(text, SegMode.INDEX).get(x).word;
                 	 if(!word.equals(" ")&&t.equals(word))
                     {//过滤掉重复和空格
                 		count++;
                     }
                  }*/
                  Tct[j]=count;
                  j++;
              }     
              //计算条件概率的估计值为t在c类文档中出现的相对频率
              System.out.println("计算条件概率中....");
              double Sigma=0;
              for(int x=0;x<Tct.length;x++)
              {
                  Sigma+=Tct[x];       
              }     
              for(int t=0;t<V.size();t++)
              {
                  condprob[t][i]=(Tct[t]+1)/(Sigma+V.size());
              }  
              i++;
          }  
          Node node=new Node();          
          node.V=V;
          node.prior=prior;
          node.condprob=condprob;  
          return node;
      }
      public static double[] test(ArrayList<String> classSet,ArrayList<String> V,double[] prior,
                                     double[][]condprob,TreeMap<String,String> Set)
      {  
          double[] scores=new double[classSet.size()];
          int correct_num=0;
          //for(int i=0;i<testingSet.size();i++)
          for(Entry<String, String> entry:Set.entrySet())
          {//遍历测试集
              ArrayList<String> W=new ArrayList<String>();
              // EXTRACTOKENSFROMDoc,将文档d中的单词抽取出来，允许重复，如果单词是全新的，在全局单词表V中都没出现过，则忽略掉
             // String str=testingSet.get(i);
              String str=entry.getKey();
              str=str.toLowerCase();
              JiebaSegmenter segmenter = new JiebaSegmenter();
              for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
              {
             	 	String word=segmenter.process(str, SegMode.INDEX).get(j).word;
             	 	if(V.contains(word))
             	 	{
             	 		//System.out.println("2"+word);
             	 		W.add(word);
             	 	}
        	 }
              
          
            //计算后验概率        
              int index=0;
              scores=new double[classSet.size()];
              for(int j=0;j<classSet.size();j++)
              {
                  scores[j]=Math.log(prior[j]);           
                  for(String t:W)
                  {
                      for(String word:V)
                      {
                          if(t.equals(word))
                          {                
                             index=V.indexOf(word);
                          }
                      }
                      scores[j]+=Math.log(condprob[index][j]);      
                  }
              }
            
              if(scores[0]>scores[1])
              {                      //比较两个最大后验概率，
                  //System.out.println("测试集属于类yes");
            	  System.out.println("属于非讲座类");
            	  if(entry.getValue().equals("0"))
            	  {
            		  correct_num++;
            	  }
              }
              else
              {
                  System.out.println("属于讲座类");
                  if(entry.getValue().equals("1"))
            	  {
            		  correct_num++;
            	  }
              }
          } 
          System.out.println(correct_num*0.01);
          return scores;
      }
      public static void train_node(ArrayList<String> classSet,String file_name) throws IOException
      {
    	  TreeMap<String,String> trainingSet=new TreeMap<String,String>();
          readfile(trainingSet,file_name);
          Node node=new Node();
          node=train(classSet,trainingSet);
          
          try {  
              ObjectOutputStream os = new ObjectOutputStream(  
                      new FileOutputStream("node.dat"));  
              os.writeObject(node);// 将User对象写进文件  
              os.close();  
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          } 
      }
      public static void main(String[] args) throws IOException
      {
    	  
          ArrayList<String> classSet=new ArrayList<String>();
         // ArrayList<String> testingSet=new ArrayList<String>();
     
          //初始化类别集合
          classSet.add("0");
          classSet.add("1");
          //train_node(classSet,"训练集.txt");
          /*
          readfile(trainingSet,"训练集.txt");
          Node node=new Node();
          node=train(classSet,trainingSet);
          
          try {  
              ObjectOutputStream os = new ObjectOutputStream(  
                      new FileOutputStream("node.dat"));  
              os.writeObject(node);// 将User对象写进文件  
              os.close();  
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          } */ 
          Node node=new Node();
          try {  
              ObjectInputStream is = new ObjectInputStream(new FileInputStream(  
                      "node.dat"));  
              node = (Node) is.readObject();// 从流中读取User的数据  
              is.close();  
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          } catch (ClassNotFoundException e) {  
              e.printStackTrace();  
          }  
          
          //初始化测
          TreeMap<String,String> testingSet=new TreeMap<String,String>();
         // testingSet.add("讲座");   
          
          readfile(testingSet,"测试集.txt");
          
          double[] scores;
          scores=test(classSet,node.V,node.prior,node.condprob,testingSet);
          //输出结果
          /*for(double score:scores)
          {
              System.out.println(score);
          } */
      }
}
