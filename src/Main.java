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
    double[] prior;//�������
    double[][] condprob;//��������
    Node()
    {
        V=new ArrayList<String>();
    }
}
public class Main
//���ر�Ҷ˹����
{
      public static Node train(ArrayList<String> classSet, TreeMap<String,String> trainingSet)
      {//ͨ��ѵ������ȷ���������������ʺ���������,����Ϊ�༯��,ѵ����
          ArrayList<String> V=new ArrayList<String>();
          for(int i=0;i<trainingSet.size();i++)
          {//��ѵ�����е����¼��뵽�ĵ�������
              for(Entry<String, String> entry:trainingSet.entrySet())
              {
                 String str=entry.getKey();//��ȡÿƪ���µ�����
                 str=str.toLowerCase();//��׼����������
                 JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
                 for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
                 {
                	 String word=segmenter.process(str, SegMode.INDEX).get(j).word;
                	 if(!V.contains(word)&&!word.equals(" "))
                     {//���˵��ظ��Ϳո�
                		 V.add(word);
                     }
           	     }
              }
          }
          
          double N=trainingSet.size();//�ĵ�����
          double[] prior=new double[classSet.size()];//�������
          double[][] condprob=new double[V.size()][classSet.size()];//��������
          int i=0;
          for(String c:classSet)
          {
              //CountDocsInClass,����Nc��NcΪѵ��������c �����������ĵ���Ŀ
              double Nc=0;//������ĵ���Ŀ
              String text="";//������ĵ�
              for(Entry<String, String> entry:trainingSet.entrySet())
              {//�������¼���
                  if(entry.getValue().equals(c))
                  {
                      Nc++;
                      text+=entry.getKey().toLowerCase();//�����c�µ��ĵ����ӳ�һ�����ַ���,�Կո����
                      text+=" ";
                  } 
              }
              prior[i]=Nc/N;//�����������
              //String[] texts=text.split(" ");//���ݿո��и��ַ���,���ÿƪ����
              double[] Tct=new double[V.size()];//ÿ�����ʳ��ֵĴ���      
              int j=0;
              for(String t:V)
              {
                  double count=0;
                  JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
                  for(int x=0;x<segmenter.process(text, SegMode.SEARCH).size();x++)
                  {
                 	 String word=segmenter.process(text, SegMode.INDEX).get(x).word;
                 	 if(!word.equals(" ")&&t.equals(word))
                     {//���˵��ظ��Ϳո�
                 		count++;
                     }
                  }
                  Tct[j]=count;
                  j++;
              }     
              //������������ �Ĺ���ֵΪt��c���ĵ��г��ֵ����Ƶ��
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
          {//�������Լ�
              ArrayList<String> W=new ArrayList<String>();
              // EXTRACTOKENSFROMDoc,���ĵ�d�еĵ��ʳ�ȡ�����������ظ������������ȫ�µģ���ȫ�ֵ��ʱ�V�ж�û���ֹ�������Ե�
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
              
          
            //����������        
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
              {                      //�Ƚ�������������ʣ�
                  System.out.println("���Լ�������yes");
              }
              else
              {
                  System.out.println("���Լ�������no");
              }
          }   
          return scores;
      }
      public static void main(String[] args)
      {
          ArrayList<String> doc=new ArrayList<String>();
          ArrayList<String> testingSet=new ArrayList<String>();
          ArrayList<String> classSet=new ArrayList<String>();
          //��ʼ��ѵ����
          doc.add("chinese beijing chinese");          //�������China
          doc.add("chinese chinese shanghai");         //�������China
          doc.add("chinese macao");                    //�������China
          doc.add("tokyo japan chinese");              //���������China
          TreeMap<String,String> trainingSet=new TreeMap<String,String>();
          for(int i=0;i<doc.size()-1;i++)
          {
              trainingSet.put(doc.get(i),"yes");
          }
          trainingSet.put(doc.get(doc.size()-1),"no");
          //��ʼ�����Լ�
          testingSet.add("chinese chinese chinese tokyo japan");    
          //��ʼ����𼯺�
          classSet.add("yes");
          classSet.add("no");
          Node node=new Node();
          node=train(classSet,trainingSet);
          double[] scores;
          scores=test(classSet,node.V,node.prior,node.condprob,testingSet);
          //������
          for(double score:scores)
          {
              System.out.println(score);
          }  
      }
}
