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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	  public static void readfile2(TreeMap<String,String> Set,ArrayList<String> class_token_1,
			  ArrayList<String> class_token_2,String file_name) throws IOException
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
	            			if(doc_class.equals("0"))
	            			{
	            				
	            				JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
	            	            List<SegToken> segTokens = segmenter.process(string, SegMode.SEARCH);
	                			for(SegToken segToken : segTokens) 
	                			{
	                				
	                				if(!class_token_1.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) 
	                				{
	                					//���кõĴʼ�������б�
	                					class_token_1.add(segToken.word);
	                				}
	                			}
	            			}
	            			if(doc_class.equals("1"))
	            			{
	            				
	            				JiebaSegmenter segmenter = new JiebaSegmenter();//ʹ����Ӣ�����Եķִʹ���
	            	            List<SegToken> segTokens = segmenter.process(string, SegMode.SEARCH);
	                			for(SegToken segToken : segTokens) 
	                			{
	                				if(!class_token_2.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) 
	                				{
	                					//���кõĴʼ�������б�
	                					class_token_2.add(segToken.word);
	                				}
	                			}
	            			}
	            		}
	            	}
	            }
	            else  
	                break;  
	        }
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
	
      public static Node train(ArrayList<String> classSet, TreeMap<String,String> trainingSet,ArrayList<String> V)
      {//ͨ��ѵ������ȷ���������������ʺ���������,����Ϊ�༯��,ѵ����
    	  /*System.out.println("�����д���....");
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
     				if(!V.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) 
     				{
     					//���кõĴʼ�������б�
     					V.add(segToken.word);
     				}
     			}
              }
          }*/
          
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
          Node node=new Node();//ѵ������ѵ�����          
          node.V=V;
          node.prior=prior;
          node.condprob=condprob;  
          return node;//����ѵ�����
      }
      public static double[] test(ArrayList<String> classSet,ArrayList<String> V,double[] prior,
                                     double[][]condprob,TreeMap<String,String> Set)
      { //����Ϊ,���༯��,ѵ��������,ѵ�����������,ѵ�����������ʵ����Ƶ��,���Լ�
          double[] scores=new double[classSet.size()];
          int correct_num=0;
          for(Entry<String, String> entry:Set.entrySet())
          {//�������Լ�
              ArrayList<String> W=new ArrayList<String>();
              String str=entry.getKey();
              str=str.toLowerCase();
              JiebaSegmenter segmenter = new JiebaSegmenter();
              for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
              {//��ȡ������ѵ�����еĲ��Ի��еĴ���
             	 	String word=segmenter.process(str, SegMode.INDEX).get(j).word;
             	 	if(V.contains(word))
             	 	{
             	 		W.add(word);
             	 	}
        	 }
              
          
            //����ô�����ÿһ���еĺ������        
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
              System.out.println(scores[0]);
              System.out.println(scores[1]);
              if(scores[0]>scores[1])
              {                   
            	  System.out.println("���ڷǽ�����");
            	  if(entry.getValue().equals("0"))
            	  {//�뱾������ͱȽ�,�����ͬ,��Ԥ����ȷ����+1
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
          System.out.println("׼ȷ��Ϊ:"+correct_num*0.01);
          return scores;
      }

      public static List<String> xstatics(Map<String,String> doc,ArrayList<String> Class,String str)
      {     //��2 ͳ����ȥ��,Class�д�ŵ����࣬doc�д�������ĵ��е�����
    	  Map<String,Double> Statistics=new HashMap<String,Double>();         //������Ŧ�2 ͳ�����÷�
    	  double N=doc.size();                       //�õ��ĵ� ��Ŀ
    	  for(String word:Class){
    	         double N11=0,N10=0,N01=0,N00=0;
    	         for(Entry<String,String> entry:doc.entrySet()){
    	            if(entry.getValue().equals(str)){
    	                if(entry.getKey().contains(word)){
    	                    N11++;          //�����������ʣ���ͬһ��
    	                }else{
    	                    N01++;          //�޴������ʣ���ͬһ��
    	                }
    	            }else{
    	               if(entry.getKey().contains(word)){
    	                   N10++;          //�����������ʵ�����ͬһ��
    	               }else{
    	                   N00++;          //�޴������ʣ�����ͬһ��
    	               }
    	            }
    	         }      
    	        double E11,E10,E00,E01;
    	         E11=N*((N11+N10)/N)*((N11+N01)/N);
    	         E10=N*((N11+N10)/N)*((N10+N00)/N);
    	         E00=N*((N01+N00)/N)*((N10+N00)/N);
    	         E01=N*((N01+N00)/N)*((N11+N01)/N);
    	         //N = N00 + N01 + N10 + N11         
    	           double statistics=Math.pow(N11-E11,2)/E11+Math.pow(N10-E10,2)/E10
    	                             +Math.pow(N00-E00,2)/E00+Math.pow(N01-E01,2)/E01;
    	           //statisticsҲ����Ϊ(N11+N10+N00+N01)*Math.pow(N11*N00-N10*N01,2)/(N11+N01)(N11+N10)(N10+N00)(N01+N00)      
    	         Statistics.put(word, statistics);       
    	     }
    	     //���ݵ÷ֽ��н�������
    	     List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(Statistics.entrySet());
    	     
    	     Collections.sort(list, new Comparator<Map.Entry<String,Double>>() 
       	  	 {      
       		  	@Override
       		  	public int compare(Map.Entry<String, Double> o1,Map.Entry<String, Double> o2) 
       		  	{  
       		  			return o2.getValue().compareTo(o1.getValue());
       		  		/*double result = o2.getValue() - o1.getValue();  
       		  		if(result > 0)  
       		  			return 1;  
       		  		else if(result == 0)  
       		  			return 0;  
       		  		else   
       		  			return -1;*/ 
       	     	}  
       	  	 });
    	
       	         
       	 
    	     ArrayList<String> temp=new ArrayList<String>();
    	     for(Map.Entry<String, Double> set:list){
    	            temp.add(set.getKey());
    	     }
    	     List<String> category=new ArrayList<String>();
    	     category=temp.subList(0, 999);                     //ȡǰһ�ٸ�����2 ͳ�����÷���ߵĴ���
    	     return category;   
      }
      public static List<String> MI(Map<String,String> doc,ArrayList<String> Class,String str)
      {//����Ϣȥ��
       //ѵ����doc,���е�token,����
    	  Map<String,Double> utilityIndex=new HashMap<String,Double>();//������Ż���Ϣ�÷�
    	  double N=doc.size();//�õ��ĵ� ��Ŀ
    	  for(String word:Class)
    	  {
    		  double N11=0,N10=0,N01=0,N00=0;
    	      for(Entry<String,String> entry:doc.entrySet())
    	      {
    	    	  if(entry.getValue().equals(str))
    	    	  {
    	    		  if(entry.getKey().contains(word))
    	    		  {
    	    			  N11++;//�����������ʣ���ͬһ��
    	              }
    	    		  else
    	    		  {
    	                  N01++;//�޴������ʣ���ͬһ��
    	              }
    	            }
    	    	  else
    	    	  {
    	              if(entry.getKey().contains(word))
    	              {
    	            	  N10++;//�����������ʵ�����ͬһ��
    	              }
    	              else
    	              {
    	            	  N00++;//�޴������ʣ�����ͬһ��
    	              }
    	          }
    	       }      
    	       //N = N00 + N01 + N10 + N11         
    	       double utilityindex=(N11/N)*(Math.log((N*N11)/((N11+N10)*(N01+N11)))/Math.log(2))+   //ln0������
    	                           (N01/N)*(Math.log((N*N01)/((N00+N01)*(N01+N11)))/Math.log(2))+
    	                           (N10/N)*(Math.log((N*N10)/((N11+N10)*(N00+N10)))/Math.log(2))+
    	                           (N00/N)*(Math.log((N*N00)/((N00+N01)*(N10+N00)))/Math.log(2)); 
    	       if(N00==0|N11==0|N10==0|N01==0)
    	       {//Ϊ0ʱutilityindex�᲻���ڣ�ΪNaN
    	             utilityindex=0;
    	       }
    	       utilityIndex.put(word, utilityindex);           
    	  }
    	     //���ݵ÷ֽ��н�������
    	  List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(utilityIndex.entrySet());
    	  Collections.sort(list, new Comparator<Map.Entry<String,Double>>() 
    	  {      
    		  @Override
    	      public int compare(Map.Entry<String, Double> o1,Map.Entry<String, Double> o2) 
    		  {  
    			  double result = o2.getValue() - o1.getValue();  
    	          if(result > 0)  
    	        	  return 1;  
    	          else if(result == 0)  
    	              return 0;  
    	          else   
    	        	  return -1; 
    	      }
    	         
    	  });  
    	  ArrayList<String> temp=new ArrayList<String>();
    	  for(Map.Entry<String, Double> set:list)
    	  {
    		  temp.add(set.getKey());
    	  }
    	  List<String> category=new ArrayList<String>();
    	  category=temp.subList(0, 999);//ȡǰһǧ������Ϣ�÷���ߵĴ���
    	  return category;   
      }
      public static void train_node(ArrayList<String> classSet,String file_name) throws IOException
      {
    	  TreeMap<String,String> trainingSet=new TreeMap<String,String>();
    	  ArrayList<String> class_token_1=new ArrayList<String>();
    	  ArrayList<String> class_token_2=new ArrayList<String>();
          readfile2(trainingSet,class_token_1,class_token_2,file_name);
          
        /*//����Ϣȥ�봦��
          System.out.println("M1����Ϣȥ����......");
          List<String>MI1=MI(trainingSet,class_token_1,"0");
          System.out.println("M2����Ϣȥ����......");
          List<String>MI2=MI(trainingSet,class_token_2,"1");*/
        //x^2ȥ�봦��
          System.out.println("M1X^2ȥ����......");
          List<String>MI1=xstatics(trainingSet,class_token_1,"0");
          System.out.println("M2X^2ȥ����......");
          List<String>MI2=xstatics(trainingSet,class_token_2,"1");
          ArrayList<String> V=new ArrayList<String>();  
          V.addAll(MI1) ;
          for(String word:MI2){
              if(!V.contains(word)){
                  V.add(word);
              }
          }
          System.out.println("ȥ��ɹ�");
          Node node=new Node();
          node=train(classSet,trainingSet,V);
          
          try {  
              ObjectOutputStream os = new ObjectOutputStream(  
                      new FileOutputStream("node.dat"));  
              os.writeObject(node);//��������ļ�  
              os.close();  
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          }
          System.out.println("ѵ�����!");
      }
      public static void main(String[] args) throws IOException
      {
    	  
          ArrayList<String> classSet=new ArrayList<String>();
     
          //��ʼ����𼯺�
          classSet.add("0");
          classSet.add("1");
          train_node(classSet,"ѵ����.txt");//ִ��ѵ��
      
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
