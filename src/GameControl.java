import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.OptionalInt;


public class GameControl {
	int numOfPlayers;
	int roundsRemaining;
	HashSet<Section> mapOfGame;
	HashMap<Integer, HashMap<Integer,Section>> sectionMap;
	int[] moneyOfPlayers;
	int[] numOfFighters;
	HashMap<Integer,HashMap<Integer,Fighter>> fightersOfPlayers;
	HashMap<Integer,HashSet<Section>> sectionsOccupiedByPlayers;
	int occupyCost;
	int fighterCost;
	int defendArea;
	Boolean[] alive;
	
	public GameControl(int players,int rounds,HashMap<Integer, HashMap<Integer,Section>> mapMap,int occupyParam,int initialMoneyParam,int fighterParam,int defendParam){
		numOfPlayers=players;
		roundsRemaining=rounds;
		sectionMap=mapMap;
		mapOfGame=new HashSet<>();
		mapMap.values().stream().forEach(m->mapOfGame.addAll(m.values()));
		occupyCost=occupyParam;
		fighterCost=fighterParam;
		defendArea=defendParam;
		moneyOfPlayers=new int[numOfPlayers];
		Arrays.fill(moneyOfPlayers, initialMoneyParam);
		numOfFighters=new int[numOfPlayers];
		Arrays.fill(numOfFighters, 0);
		fightersOfPlayers=new HashMap<Integer,HashMap<Integer,Fighter>>();
		for(int i=1;i<=numOfPlayers;i++){
			fightersOfPlayers.put(i, new HashMap<Integer,Fighter>());
		}
		sectionsOccupiedByPlayers=new HashMap<Integer,HashSet<Section>>();
		for(int i=1;i<=numOfPlayers;i++){
			int j=i;
			sectionsOccupiedByPlayers.put(i, new HashSet<Section>());
			mapOfGame.stream().filter(s->s.status==j).forEach((sectionsOccupiedByPlayers.get(j))::add);
		}
		alive=new Boolean[numOfPlayers];
		Arrays.fill(alive, true);
	}
	
	public boolean moveAllowed(int player,int fighter,int x,int y){
		refresh(player, fighter);
		if(fightersOfPlayers.get(player).get(fighter).moveToSections.contains(sectionMap.get(x).get(y))) return true;
		else return false;
	}
	
	public void move(int player,int fighter,int x,int y){
		Fighter target=fightersOfPlayers.get(player).get(fighter);
		Section sect=sectionMap.get(target.x).get(target.y);
		sect.onSect=null;
		target.x=x;
		target.y=y;
		sect=sectionMap.get(target.x).get(target.y);
		sect.onSect=target;
	}
	
	public boolean occupyAllowed(int player,int fighter,String direction){
		refresh(player, fighter);
		if(fightersOfPlayers.get(player).get(fighter).occupyDirections.contains(direction)&&moneyOfPlayers[player-1]>=occupyCost) return true;
		else return false;
	}
	
	public void occupy(int player,int fighter,String direction){
		Section[] sects=new Section[1];
		for(Section s:getOccupyRange(fightersOfPlayers.get(player).get(fighter).type, direction, fightersOfPlayers.get(player).get(fighter).x, fightersOfPlayers.get(player).get(fighter).y).toArray(sects)){
			if(s.status!=player){
				sectionsOccupiedByPlayers.get(s.status).remove(s);
				s.status=player;
				sectionsOccupiedByPlayers.get(s.status).add(s);
				if(s.onSect!=null){
					if(s.onSect.level<fightersOfPlayers.get(player).get(fighter).level){
						fightersOfPlayers.get(s.onSect.player).remove(s.onSect.fighterNum);
						s.onSect=null;
					}
				}
			}
		}
		sects=null;
		moneyOfPlayers[player-1]-=occupyCost;
	}
	
	public boolean upgradeAllowed(int player,int fighter){
		if(moneyOfPlayers[player-1]>=(fightersOfPlayers.get(player).get(fighter).level/2+0.5)*fighterCost) return true;
		else return false;
	}
	
	public void upgrade(int player,int fighter){
		fightersOfPlayers.get(player).get(fighter).level++;
		moneyOfPlayers[player-1]-=(fightersOfPlayers.get(player).get(fighter).level/2+0.5)*fighterCost;
	}
	
	public boolean purchaseAllowed(int player,String typeOfFighter){
		if(moneyOfPlayers[player-1]>=fighterCost) return true;
		else return false;
	}
	
	public void purchase(int player,String typeOfFighter,int x,int y){
		fightersOfPlayers.get(player).put(numOfFighters[player-1]+1, new Fighter(numOfFighters[player-1]+1, player, x, y, typeOfFighter, 1));
		numOfFighters[player-1]++;
		Section sect=sectionMap.get(x).get(y);
		sect.onSect=fightersOfPlayers.get(player).get(numOfFighters[player-1]);
		moneyOfPlayers[player-1]-=fighterCost;
		sect=null;
	}
	
	public boolean availableForNewFighter(int player,int x,int y){
		Section sect=sectionMap.get(x).get(y);
		if(sect.status==player&&sect.onSect==null) return true;
		else return false;
	}
	
	public void refresh(int player,int fighter){
		Fighter f=fightersOfPlayers.get(player).get(fighter);
		HashSet<Section> set=f.moveToSections;
		set.clear();
		Section sect=sectionMap.get(f.x).get(f.y);
		set.add(sect);
		HashSet<Section> temp=new HashSet<Section>();
		while(true){
			//Collections.addAll(temp,(Section[])sectionsOccupiedByPlayers.get(player).stream().filter(s->temp.stream().filter(p->Math.abs(p.x-s.x)+Math.abs(p.y-s.y)==1).count()>0).toArray());
			//set.addAll(temp);
			temp.addAll(set);
			temp.stream().map(s->sectionMap.get(s.x)==null?s:sectionMap.get(s.x).get(s.y-1)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			temp.stream().map(s->sectionMap.get(s.x)==null?s:sectionMap.get(s.x).get(s.y+1)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			temp.stream().map(s->sectionMap.get(s.x-1)==null?s:sectionMap.get(s.x-1).get(s.y)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			temp.stream().map(s->sectionMap.get(s.x+1)==null?s:sectionMap.get(s.x+1).get(s.y)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			if(temp.size()==set.size()) break;
			//else temp.clear();
		}
		set.removeIf(s->s.onSect!=null);
		f.occupyDirections.clear();
		int count=0;
		if(defendArea==8){
		for(String dir:new String[]{"e","w","s","n"}){
			int kkk;
			if(getOccupyRange(f.type, dir, f.x, f.y).stream().filter(s->(s.status!=sect.status)&&(s.onSect==null||s.onSect.level<f.level)&&(mapOfGame.stream().filter(p->Math.abs(p.x-s.x)<2&&Math.abs(p.y-s.y)<2).allMatch(p->p.onSect==null||p.onSect.level<=sect.onSect.level))).count()>0&&!getOccupyRange(f.type, dir, f.x, f.y).stream().filter(s->s.onSect!=null).anyMatch(s->s.onSect.player!=player&&s.onSect.level>=sect.onSect.level)){
				f.occupyDirections.add(dir);
			}
		}
		}
		else{
			for(String dir:new String[]{"e","w","s","n"}){
				if(getOccupyRange(f.type, dir, f.x, f.y).stream().filter(s->(s.status!=sect.status)&&(s.onSect==null||s.onSect.level<f.level)&&(mapOfGame.stream().filter(p->Math.abs(p.x-s.x)+Math.abs(p.y-s.y)<2).allMatch(p->p.onSect==null||p.onSect.level<=sect.onSect.level))).count()>0&&!getOccupyRange(f.type, dir, f.x, f.y).stream().filter(s->s.onSect!=null).anyMatch(s->s.onSect.player!=player&&s.onSect.level>=sect.onSect.level)){
					f.occupyDirections.add(dir);
				}
			}
		}
	}
	
	public HashSet<Section> getOccupyRange(String type,String direction,int x,int y){//只考虑坐标是否在地图内
		HashSet<Section> result=new HashSet<Section>();
		result.addAll(mapOfGame);
		if(type.equals("s")){
			if(direction.equals("s")){
				result.removeIf(sz->!(sz.x==x&&sz.y-y>0&&sz.y-y<5));
			}
			if(direction.equals("n")){
				result.removeIf(sz->!(sz.x==x&&sz.y-y<0&&sz.y-y>-5));
			}
			if(direction.equals("w")){
				result.removeIf(sz->!(sz.y==y&&sz.x-x<0&&sz.x-x>-5));
			}
			if(direction.equals("e")){
				result.removeIf(sz->!(sz.y==y&&sz.x-x>0&&sz.x-x<5));
			}
			
		}
		else if(type.equals("w")){
			if(direction.equals("s")){
				result.removeIf(sz->!(sz.x-x>=0&&sz.y-y>=0&&Math.abs(sz.x-x)+Math.abs(sz.y-y)<3));
			}
			if(direction.equals("n")){
				result.removeIf(sz->!(sz.x-x<=0&&sz.y-y<=0&&Math.abs(sz.x-x)+Math.abs(sz.y-y)<3));
			}
			if(direction.equals("w")){
				result.removeIf(sz->!(sz.x-x<=0&&sz.y-y>=0&&Math.abs(sz.x-x)+Math.abs(sz.y-y)<3));
			}
			if(direction.equals("e")){
				result.removeIf(sz->!(sz.x-x>=0&&sz.y-y<=0&&Math.abs(sz.x-x)+Math.abs(sz.y-y)<3));
			}
		}
		else{
			if(direction.equals("s")){
				result.removeIf(sz->Math.abs(sz.x-x)>1||Math.abs(sz.y-y)>1||(sz.x==x&&sz.y+1==y));
			}
			if(direction.equals("n")){
				result.removeIf(sz->Math.abs(sz.x-x)>1||Math.abs(sz.y-y)>1||(sz.x==x&&sz.y-1==y));
			}
			if(direction.equals("w")){
				result.removeIf(sz->Math.abs(sz.x-x)>1||Math.abs(sz.y-y)>1||(sz.y==y&&sz.x-1==x));
			}
			if(direction.equals("e")){
				result.removeIf(sz->Math.abs(sz.x-x)>1||Math.abs(sz.y-y)>1||(sz.y==y&&sz.x+1==x));
			}
		}
		if(direction.equals("a")){
			HashSet<Section> set=new HashSet<>();
			set.addAll(getOccupyRange(type, "s", x, y));
			set.addAll(getOccupyRange(type, "n", x, y));
			set.addAll(getOccupyRange(type, "w", x, y));
			set.addAll(getOccupyRange(type, "e", x, y));
			return set;
		}
		if(result.size()==400) System.out.println(x+","+y);
		return result;
	}
	
	public int movePathLength(int player,int fighter,int x,int y){
		Fighter f=fightersOfPlayers.get(player).get(fighter);
		HashSet<Section> set=new HashSet<Section>();
		Section sect=sectionMap.get(f.x).get(f.y);
		Section destination=sectionMap.get(x).get(y);
		set.add(sect);
		HashSet<Section> temp=new HashSet<Section>();
		int length=0;
		while(true){
			temp.addAll(set);
			temp.stream().map(s->sectionMap.get(s.x)==null?s:sectionMap.get(s.x).get(s.y-1)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			temp.stream().map(s->sectionMap.get(s.x)==null?s:sectionMap.get(s.x).get(s.y+1)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			temp.stream().map(s->sectionMap.get(s.x-1)==null?s:sectionMap.get(s.x-1).get(s.y)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			temp.stream().map(s->sectionMap.get(s.x+1)==null?s:sectionMap.get(s.x+1).get(s.y)).filter(s->s!=null&&sectionsOccupiedByPlayers.get(player).contains(s)).forEach(set::add);
			length++;
			if(set.contains(destination)) break;
			//else temp.clear();
		}
		
		return length;
	}
	public int getWinner(){
		OptionalInt max=sectionsOccupiedByPlayers.values().stream().mapToInt(s->s.size()).max();
		int sectionMax=max.getAsInt();
		Object[] win=sectionsOccupiedByPlayers.keySet().stream().filter(s->sectionsOccupiedByPlayers.get(s).size()==sectionMax).toArray();
		//System.out.println((Integer)win[0]);
		if(win.length>1) return 0;
		else return (Integer)win[0];
	}
}

