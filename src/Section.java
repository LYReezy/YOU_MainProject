import java.util.HashMap;

public class Section {
	int x;
	int y;
	int status;
	Fighter onSect;
	
	public Section(int xx,int yy){
		x=xx;
		y=yy;
		status=0;
		onSect=null;
	}
	
}

class MapGenerator{
	HashMap<Integer, HashMap<Integer,Section>> generateRandomMap(int numOfPlayers,int width,int height){//�����������ͼ��ȣ���ͼ�߶�
		HashMap<Integer, HashMap<Integer,Section>> map=new HashMap<Integer, HashMap<Integer,Section>>();//����Ĵ������ʾ���ã���Ҫ�ĵ�
		for(int i=1;i<21;i++){
			map.put(i, new HashMap<Integer,Section>());
			for(int j=1;j<21;j++){
				map.get(i).put(j, new Section(i, j));
			}
		}
		
		return map;//ʵ�������������������ĵ�ͼ���ص������ú�status�����hashmap��key��Ӧ�У��ڲ�hashmap��key��Ӧ�У���ͼ��һ��Ϊ���Σ������ڵ����鲻������Ӧ��section����;
	}//������Ϊ1��2��3...��ĳ����ҿ��Ƶ������status���ڸ������ţ�δ�����Ƶ�����status����0
}