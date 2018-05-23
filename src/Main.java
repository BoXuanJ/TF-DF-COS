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
	            			if(doc_class.equals("0"))
	            			{
	            				
	            				JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
	            	            List<SegToken> segTokens = segmenter.process(string, SegMode.SEARCH);
	                			for(SegToken segToken : segTokens) 
	                			{
	                				
	                				if(!class_token_1.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) 
	                				{
	                					//将切好的词加入词项列表
	                					class_token_1.add(segToken.word);
	                				}
	                			}
	            			}
	            			if(doc_class.equals("1"))
	            			{
	            				
	            				JiebaSegmenter segmenter = new JiebaSegmenter();//使用中英都可以的分词工具
	            	            List<SegToken> segTokens = segmenter.process(string, SegMode.SEARCH);
	                			for(SegToken segToken : segTokens) 
	                			{
	                				if(!class_token_2.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) 
	                				{
	                					//将切好的词加入词项列表
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
	
      public static Node train(ArrayList<String> classSet, TreeMap<String,String> trainingSet,ArrayList<String> V)
      {//通过训练集来确定各个类的先验概率和条件概率,参数为类集合,训练集
    	  /*System.out.println("文章切词中....");
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
     				if(!V.contains(segToken.word)&&!segToken.word.equals(" ")&&!segToken.word.isEmpty() && !isChineseSymbol(segToken.word)) 
     				{
     					//将切好的词加入词项列表
     					V.add(segToken.word);
     				}
     			}
              }
          }*/
          
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
          Node node=new Node();//训练集的训练结果          
          node.V=V;
          node.prior=prior;
          node.condprob=condprob;  
          return node;//返回训练结果
      }
      public static double[] test(ArrayList<String> classSet,ArrayList<String> V,double[] prior,
                                     double[][]condprob,TreeMap<String,String> Set)
      { //参数为,分类集合,训练集文章,训练集先验概率,训练集条件概率的相对频率,测试集
          double[] scores=new double[classSet.size()];
          int correct_num=0;
          for(Entry<String, String> entry:Set.entrySet())
          {//遍历测试集
              ArrayList<String> W=new ArrayList<String>();
              String str=entry.getKey();
              str=str.toLowerCase();
              JiebaSegmenter segmenter = new JiebaSegmenter();
              for(int j=0;j<segmenter.process(str, SegMode.SEARCH).size();j++)
              {//获取出现在训练集中的测试机中的词条
             	 	String word=segmenter.process(str, SegMode.INDEX).get(j).word;
             	 	if(V.contains(word))
             	 	{
             	 		W.add(word);
             	 	}
        	 }
              
          
            //计算该词条在每一类中的后验概率        
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
            	  System.out.println("属于非讲座类");
            	  if(entry.getValue().equals("0"))
            	  {//与本身的类型比较,如果相同,则预测正确个数+1
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
          System.out.println("准确率为:"+correct_num*0.01);
          return scores;
      }

      public static List<String> xstatics(Map<String,String> doc,ArrayList<String> Class,String str)
      {     //χ2 统计量去噪,Class中存放的是类，doc中存放所有文档中的内容
    	  Map<String,Double> Statistics=new HashMap<String,Double>();         //用来存放χ2 统计量得分
    	  double N=doc.size();                       //得到文档 数目
    	  for(String word:Class){
    	         double N11=0,N10=0,N01=0,N00=0;
    	         for(Entry<String,String> entry:doc.entrySet()){
    	            if(entry.getValue().equals(str)){
    	                if(entry.getKey().contains(word)){
    	                    N11++;          //包含此特征词，是同一类
    	                }else{
    	                    N01++;          //无此特征词，是同一类
    	                }
    	            }else{
    	               if(entry.getKey().contains(word)){
    	                   N10++;          //包含此特征词但不是同一类
    	               }else{
    	                   N00++;          //无此特征词，不是同一类
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
    	           //statistics也可以为(N11+N10+N00+N01)*Math.pow(N11*N00-N10*N01,2)/(N11+N01)(N11+N10)(N10+N00)(N01+N00)      
    	         Statistics.put(word, statistics);       
    	     }
    	     //根据得分进行降序排序
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
    	     category=temp.subList(0, 999);                     //取前一百个、χ2 统计量得分最高的词项
    	     return category;   
      }
      public static List<String> MI(Map<String,String> doc,ArrayList<String> Class,String str)
      {//互信息去噪
       //训练集doc,类中的token,类名
    	  Map<String,Double> utilityIndex=new HashMap<String,Double>();//用来存放互信息得分
    	  double N=doc.size();//得到文档 数目
    	  for(String word:Class)
    	  {
    		  double N11=0,N10=0,N01=0,N00=0;
    	      for(Entry<String,String> entry:doc.entrySet())
    	      {
    	    	  if(entry.getValue().equals(str))
    	    	  {
    	    		  if(entry.getKey().contains(word))
    	    		  {
    	    			  N11++;//包含此特征词，是同一类
    	              }
    	    		  else
    	    		  {
    	                  N01++;//无此特征词，是同一类
    	              }
    	            }
    	    	  else
    	    	  {
    	              if(entry.getKey().contains(word))
    	              {
    	            	  N10++;//包含此特征词但不是同一类
    	              }
    	              else
    	              {
    	            	  N00++;//无此特征词，不是同一类
    	              }
    	          }
    	       }      
    	       //N = N00 + N01 + N10 + N11         
    	       double utilityindex=(N11/N)*(Math.log((N*N11)/((N11+N10)*(N01+N11)))/Math.log(2))+   //ln0不存在
    	                           (N01/N)*(Math.log((N*N01)/((N00+N01)*(N01+N11)))/Math.log(2))+
    	                           (N10/N)*(Math.log((N*N10)/((N11+N10)*(N00+N10)))/Math.log(2))+
    	                           (N00/N)*(Math.log((N*N00)/((N00+N01)*(N10+N00)))/Math.log(2)); 
    	       if(N00==0|N11==0|N10==0|N01==0)
    	       {//为0时utilityindex会不存在，为NaN
    	             utilityindex=0;
    	       }
    	       utilityIndex.put(word, utilityindex);           
    	  }
    	     //根据得分进行降序排序
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
    	  category=temp.subList(0, 999);//取前一千个互信息得分最高的词项
    	  return category;   
      }
      public static void train_node(ArrayList<String> classSet,String file_name) throws IOException
      {
    	  TreeMap<String,String> trainingSet=new TreeMap<String,String>();
    	  ArrayList<String> class_token_1=new ArrayList<String>();
    	  ArrayList<String> class_token_2=new ArrayList<String>();
          readfile2(trainingSet,class_token_1,class_token_2,file_name);
          
        /*//互信息去噪处理
          System.out.println("M1互信息去噪中......");
          List<String>MI1=MI(trainingSet,class_token_1,"0");
          System.out.println("M2互信息去噪中......");
          List<String>MI2=MI(trainingSet,class_token_2,"1");*/
        //x^2去噪处理
          System.out.println("M1X^2去噪中......");
          List<String>MI1=xstatics(trainingSet,class_token_1,"0");
          System.out.println("M2X^2去噪中......");
          List<String>MI2=xstatics(trainingSet,class_token_2,"1");
          ArrayList<String> V=new ArrayList<String>();  
          V.addAll(MI1) ;
          for(String word:MI2){
              if(!V.contains(word)){
                  V.add(word);
              }
          }
          System.out.println("去噪成功");
          Node node=new Node();
          node=train(classSet,trainingSet,V);
          
          try {  
              ObjectOutputStream os = new ObjectOutputStream(  
                      new FileOutputStream("node.dat"));  
              os.writeObject(node);//将对象进文件  
              os.close();  
          } catch (FileNotFoundException e) {  
              e.printStackTrace();  
          } catch (IOException e) {  
              e.printStackTrace();  
          }
          System.out.println("训练完毕!");
      }
      public static void main(String[] args) throws IOException
      {
    	  
          ArrayList<String> classSet=new ArrayList<String>();
     
          //初始化类别集合
          classSet.add("0");
          classSet.add("1");
          train_node(classSet,"训练集.txt");//执行训练
      
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
