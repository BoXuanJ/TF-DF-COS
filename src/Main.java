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
	
	private static boolean isChineseSymbol(String text) {
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
		if(text.equals("��")) return true;
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
	            		if(head.equals("#="))//���
	            		{
	            			if(string.charAt(3)=='1')
	            			{//������ѧ������
	            				doc_class="1";
	            			}
	            			if(string.charAt(3)=='0')
	            			{//����ѧ������
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
      {//ͨ��ѵ������ȷ���������������ʺ���������,����Ϊ�༯��,ѵ����
    	  System.out.println("�����д���....");
          ArrayList<String> V=new ArrayList<String>();
          for(int i=0;i<trainingSet.size();i++)
          {//��ѵ�����е����¼��뵽�ĵ�������
              for(Entry<String, String> entry:trainingSet.entrySet())
              {
                 String str=entry.getKey();//��ȡÿƪ���µ�����
                 //System.out.println(str);
                 str=str.toLowerCase();//��׼����������
                 JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
               
     			 List<SegToken> segTokens = segmenter.process(str, SegMode.SEARCH);
     			 for(SegToken segToken : segTokens) 
     			 {
     				if(!V.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) {
     					//���кõĴʼ�������б�
     					V.add(segToken.word);
     				}
     			}

                 
                 /*JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
                 for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
                 {
                	 String word=segmenter.process(str, SegMode.INDEX).get(j).word;
                	 if(!V.contains(word)&&!word.equals(" "))
                     {//���˵��ظ��Ϳո�
                		 V.add(word);
                		// System.out.println(word);
                     }
           	     }*/
              }
          }
          
          double N=trainingSet.size();//�ĵ�����
          double[] prior=new double[classSet.size()];//�������
          double[][] condprob=new double[V.size()][classSet.size()];//��������
          int i=0;
          for(String c:classSet)
          {//dasdas
              //CountDocsInClass,����Nc��NcΪѵ��������c �����������ĵ���Ŀ
              double Nc=0;//������ĵ���Ŀ
              String text="";//������ĵ�
              System.out.println("��������Ƶ����....");
              for(Entry<String, String> entry:trainingSet.entrySet())
              {//�������¼���
                  if(entry.getValue().equals(c))
                  {//����ÿһ���е���������
                      Nc++;
                      text+=entry.getKey().toLowerCase();
                      //�����c�µ��ĵ����ӳ�һ�����ַ���,�Կո����
                      text+=" ";
                  } 
              }
              prior[i]=Nc/N;//�����������
       
              double[] Tct=new double[V.size()];//ÿ�����ʳ��ֵĴ���      
              int j=0;
              System.out.println("�������������д���....");
              for(String t:V)
              {
                  double count=0;
                  JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
                  
                  List<SegToken> segTokens = segmenter.process(text, SegMode.SEARCH);
      			  for(SegToken segToken : segTokens) 
      			  {
      				  if(t.equals(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) {
      					  //���кõĴʼ�������б�
      					  count++;
      				  }
      			  }
                  
                  /*for(int x=0;x<segmenter.process(text, SegMode.SEARCH).size();x++)
                  {
                 	 String word=segmenter.process(text, SegMode.INDEX).get(x).word;
                 	 if(!word.equals(" ")&&t.equals(word))
                     {//���˵��ظ��Ϳո�
                 		count++;
                     }
                  }*/
                  Tct[j]=count;
                  j++;
              }     
              //�����������ʵĹ���ֵΪt��c���ĵ��г��ֵ����Ƶ��
              System.out.println("��������������....");
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
          {//�������Լ�
              ArrayList<String> W=new ArrayList<String>();
              // EXTRACTOKENSFROMDoc,���ĵ�d�еĵ��ʳ�ȡ�����������ظ������������ȫ�µģ���ȫ�ֵ��ʱ�V�ж�û���ֹ�������Ե�
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
                  //System.out.println("���Լ�������yes");
            	  System.out.println("���ڷǽ�����");
            	  if(entry.getValue().equals("0"))
            	  {
            		  correct_num++;
            	  }
              }
              else
              {
                  System.out.println("���ڽ�����");
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
              os.writeObject(node);// ��User����д���ļ�  
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
     
          //��ʼ����𼯺�
          classSet.add("0");
          classSet.add("1");
          //train_node(classSet,"ѵ����.txt");
          /*
          readfile(trainingSet,"ѵ����.txt");
          Node node=new Node();
          node=train(classSet,trainingSet);
          
          try {  
              ObjectOutputStream os = new ObjectOutputStream(  
                      new FileOutputStream("node.dat"));  
              os.writeObject(node);// ��User����д���ļ�  
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
              node = (Node) is.readObject();// �����ж�ȡUser������  
              is.close();  
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          } catch (ClassNotFoundException e) {  
              e.printStackTrace();  
          }  
          
          //��ʼ����
          TreeMap<String,String> testingSet=new TreeMap<String,String>();
         // testingSet.add("����");   
          
          readfile(testingSet,"���Լ�.txt");
          
          double[] scores;
          scores=test(classSet,node.V,node.prior,node.condprob,testingSet);
          //������
          /*for(double score:scores)
          {
              System.out.println(score);
          } */
      }
}
