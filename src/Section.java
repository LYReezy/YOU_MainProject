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
	HashMap<Integer, HashMap<Integer,Section>> generateRandomMap(int numOfPlayers,int width,int height){//玩家数量；地图宽度；地图高度
		HashMap<Integer, HashMap<Integer,Section>> map=new HashMap<Integer, HashMap<Integer,Section>>();//下面的代码仅作示例用，需要改掉
		for(int i=1;i<21;i++){
			map.put(i, new HashMap<Integer,Section>());
			for(int j=1;j<21;j++){
				map.get(i).put(j, new Section(i, j));
			}
		}
		
		return map;//实现这个方法，生成随机的地图，重点是设置好status；外层hashmap的key对应行，内层hashmap的key对应列，地图不一定为矩形，不存在的区块不生成相应的section对象;
	}//玩家序号为1，2，3...被某个玩家控制的区块的status等于该玩家序号，未被控制的区块status等于0
}