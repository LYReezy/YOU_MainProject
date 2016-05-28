package game.you;
import java.util.HashSet;

public class Fighter {
	int fighterNum;
	int player;
	int x;
	int y;
	String type;
	int level;
	HashSet<Section> moveToSections;
	HashSet<String> occupyDirections;
	
	public Fighter(int num,int playerNum,int xx,int yy,String typeOfFighter,int levelOfFighter){
		fighterNum=num;
		player=playerNum;
		x=xx;
		y=yy;
		type=typeOfFighter;
		level=levelOfFighter;
		moveToSections=new HashSet<Section>();
		occupyDirections=new HashSet<String>();
	}
}
