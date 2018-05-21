import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

import java.util.Map.Entry;
import java.util.TreeMap;

class Node
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
      public static Node train(ArrayList<String> classSet, TreeMap<String,String> trainingSet)
      {//通过训练集来确定各个类的先验概率和条件概率,参数为类集合,训练集
          ArrayList<String> V=new ArrayList<String>();
          for(int i=0;i<trainingSet.size();i++)
          {//将训练集中的文章加入到文档集合中
              for(Entry<String, String> entry:trainingSet.entrySet())
              {
                 String str=entry.getKey();//获取每篇文章的正文
                 str=str.toLowerCase();//标准化正文内容
                 JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
                 for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
                 {
                	 String word=segmenter.process(str, SegMode.INDEX).get(j).word;
                	 if(!V.contains(word)&&!word.equals(" "))
                     {//过滤掉重复和空格
                		 V.add(word);
                     }
           	     }
              }
          }
          
          double N=trainingSet.size();//文档总数
          double[] prior=new double[classSet.size()];//先验概率
          double[][] condprob=new double[V.size()][classSet.size()];//条件概率
          int i=0;
          for(String c:classSet)
          {
              //CountDocsInClass,计算Nc，Nc为训练集合中c 类所包含的文档数目
              double Nc=0;//该类的文档数目
              String text="";//该类的文档
              for(Entry<String, String> entry:trainingSet.entrySet())
              {//遍历文章集合
                  if(entry.getValue().equals(c))
                  {
                      Nc++;
                      text+=entry.getKey().toLowerCase();//将类别c下的文档连接成一个大字符串,以空格隔开
                      text+=" ";
                  } 
              }
              prior[i]=Nc/N;//计算先验概率
              //String[] texts=text.split(" ");//根据空格切割字符串,获得每篇文章
              double[] Tct=new double[V.size()];//每个单词出现的次数      
              int j=0;
              for(String t:V)
              {
                  double count=0;
                  JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
                  for(int x=0;x<segmenter.process(text, SegMode.SEARCH).size();x++)
                  {
                 	 String word=segmenter.process(text, SegMode.INDEX).get(x).word;
                 	 if(!word.equals(" ")&&t.equals(word))
                     {//过滤掉重复和空格
                 		count++;
                     }
                  }
                  Tct[j]=count;
                  j++;
              }     
              //计算条件概率 的估计值为t在c类文档中出现的相对频率
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
                                     double[][]condprob,ArrayList<String> testingSet)
      {  
          double[] scores=new double[classSet.size()];  
          for(int i=0;i<testingSet.size();i++)
          {//遍历测试集
              ArrayList<String> W=new ArrayList<String>();
              // EXTRACTOKENSFROMDoc,将文档d中的单词抽取出来，允许重复，如果单词是全新的，在全局单词表V中都没出现过，则忽略掉
              String str=testingSet.get(i);  
              str=str.toLowerCase();
              JiebaSegmenter segmenter = new JiebaSegmenter();
              for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
              {
             	 String word=segmenter.process(str, SegMode.INDEX).get(j).word;
             	 if(V.contains(word))
                  {
             		 System.out.println("2"+word);
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
                  System.out.println("测试集属于类yes");
              }
              else
              {
                  System.out.println("测试集属于类no");
              }
          }   
          return scores;
      }
      public static void main(String[] args)
      {
          ArrayList<String> doc=new ArrayList<String>();
          ArrayList<String> testingSet=new ArrayList<String>();
          ArrayList<String> classSet=new ArrayList<String>();
          //初始化训练集
          doc.add("chinese beijing chinese");          //属于类别China
          doc.add("chinese chinese shanghai");         //属于类别China
          doc.add("chinese macao");                    //属于类别China
          doc.add("tokyo japan chinese");              //不属于类别China
          TreeMap<String,String> trainingSet=new TreeMap<String,String>();
          for(int i=0;i<doc.size()-1;i++)
          {
              trainingSet.put(doc.get(i),"yes");
          }
          trainingSet.put(doc.get(doc.size()-1),"no");
          //初始化测试集
          testingSet.add("chinese chinese chinese tokyo japan");    
          //初始化类别集合
          classSet.add("yes");
          classSet.add("no");
          Node node=new Node();
          node=train(classSet,trainingSet);
          double[] scores;
          scores=test(classSet,node.V,node.prior,node.condprob,testingSet);
          //输出结果
          for(double score:scores)
          {
              System.out.println(score);
          }  
      }
}
