package game.you;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

public class GameUI {
	static int occupyParam;
	static int fighterParam;
	static int defendParam;
	static int roundParam;
	static int initialMoneyParam;
	static double incomeParam;
	static GamePanel mainPanel;
	static IndexPage indexPage;
	static JFrame frame;
	static int hOfPanel;
	static int wOfPanel;
	
	public static void main(String[] args) {
		GameUI gameUI=new GameUI();
		gameUI.getParams();
		frame=new JFrame("SillyGame1.0");
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)screensize.getWidth();
		int height = (int)screensize.getHeight();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration()); 
		height-= screenInsets.bottom;
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setSize(width,height);
		frame.setLayout(new BorderLayout());
		
		indexPage=new IndexPage(frame);
		frame.add(indexPage, BorderLayout.CENTER);
		//System.out.println((width-frame.getInsets().left-frame.getInsets().right)+","+(height-indexPage.getInsets().bottom));
		frame.setVisible(true);
		hOfPanel=indexPage.getHeight();
		wOfPanel=indexPage.getWidth();
		indexPage.openGame();
		
		//System.out.println(wOfPanel+","+hOfPanel);
	}
	
	public void getParams(){
		occupyParam=20;
		fighterParam=20;
		defendParam=8;
		roundParam=50;
		initialMoneyParam=100;
		incomeParam=0.5;
		/*Scanner scanner=new Scanner(System.in);
		System.out.println("0:default setting; 1:customized setting");
		int setting=scanner.nextInt();
		if(setting>0){
		System.out.println("How much does occupying cost?");
		occupyParam=scanner.nextInt();
		System.out.println("How much does a fighter cost?"); 
		fighterParam=scanner.nextInt();
		System.out.println("How many sections can a fighter defend?");
		defendParam=scanner.nextInt();
		System.out.println("How many rounds at most in a game?");
		roundParam=scanner.nextInt();
		System.out.println("How much money does a player have at first?");
		initialMoneyParam=scanner.nextInt();
		System.out.println("How much money does a player get in each round for one section?");
		incomeParam=scanner.nextDouble();
		}
		scanner.close();*/
	}
	
	
	public static HashMap<Integer, HashMap<Integer,Section>> simpleMapMaker(int numOfPlayers,int width,int height){
		HashMap<Integer, HashMap<Integer,Section>> map=new HashMap<Integer, HashMap<Integer,Section>>();
		for(int i=1;i<=width;i++){
			map.put(i, new HashMap<Integer,Section>());
			for(int j=1;j<=height;j++){
				map.get(i).put(j, new Section(i, j));
				map.get(i).get(j).status=(int)(Math.random()*(numOfPlayers))+1;
			}
		}
		return map;
	}
	
}

class GamePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame mainFrame;
	int numOfPlayers;
	int widthOfMap;
	int heightOfMap;
	int rounds;
	HashSet<Section> mapOfGame;
	HashMap<Integer, HashMap<Integer,Section>> sectionMap;
	int occupyCost;
	int initialMoney;
	int fighterCost;
	int defendArea;
	double incomePerSection;
	static GameControl control;
	int wPanel;
	int hPanel;
	int wSection;
	int hSection;
	BufferedImage background;
	BufferedImage[] sectionImages;
	ImageIcon[] playerIcon;
	//JButton menuButton;
	JLabel playerLabel;
	JLabel buyFighterLabel;
	JButton spearMan;
	JButton swordMan;
	JButton axeMan;
	Font msBlack;
	JButton endThisRound;
	int currentPlayer;
	Image spearImage;
	Image swordImage;
	Image axeImage;
	Cursor spearCursor;
	Cursor swordCursor;
	Cursor axeCursor;
	Dimension cursorSize;
	Point cursorHotspot;
	int xClicked;
	int yClicked;
	boolean validClickPosition;
	boolean buyFighterClicked;
	String fighterBought;
	HashMap<String, Image> fighterImages;
	MouseInputAdapter mouseWatcher;
	Fighter fighterChosen;
	int xChosenFighter;
	int yChosenFighter;
	JLabel fighterStatusLabel;
	JButton upgradeButton;
	JButton moveButton;
	JButton occupyButton;
	HashMap<String, String> typeInChinese;
	boolean moving;
	boolean occupying;
	Cursor moveCursor;
	//Cursor occupyCursor;
	JLabel north;
	JLabel south;
	JLabel west;
	JLabel east;
	JButton cancelOccupy;
	JButton exit;
	HashMap<Integer, HashMap<Integer,JPanel>> panelsOfSections;
	int frameHeight;
	HashMap<String,ImageIcon> directArrows;
	HashMap<String, ImageIcon> diagonalArrows;
	static int winner;
	boolean gameOver;
	//JPanel afterGame;
	//JLabel resultLabel;
	int[] isAI;
	String[] fighterTypes;
	String[] directions;
	Random random;
	Fighter otherFighter;
	BufferedImage gameBackground;
	
	public GamePanel (JFrame frame,int players,int mapWidth,int mapHeight,int roundParam,HashMap<Integer, HashMap<Integer,Section>> mapMap,int occupyParam,int initialMoneyParam,int fighterParam,int defendParam,double incomeParam,int[] isAIPlayer){
		super();
		mainFrame=frame;
		numOfPlayers=players;
		widthOfMap=mapWidth;
		heightOfMap=mapHeight;
		rounds=roundParam;
		sectionMap=mapMap;
		occupyCost=occupyParam;
		initialMoney=initialMoneyParam;
		fighterCost=fighterParam;
		defendArea=defendParam;
		incomePerSection=incomeParam;
		control=new GameControl(players,roundParam , sectionMap, occupyParam, initialMoneyParam, fighterParam, defendParam);
		sectionImages=new BufferedImage[numOfPlayers];
		playerIcon=new ImageIcon[numOfPlayers];
		ArrayList<Integer> numList=new ArrayList<>();
		Collections.addAll(numList, 1,2,3,4,5,6);
		Collections.shuffle(numList);
		for(int i=1;i<=numOfPlayers;i++){
			try{
				sectionImages[i-1]=ImageIO.read(GameUI.class.getClassLoader().getResource("image/Section2-"+(numList.get(i-1))+".png"));
			}catch(IOException e){
				e.printStackTrace();
			}
			playerIcon[i-1]=new ImageIcon(sectionImages[i-1].getScaledInstance(80*GameUI.wOfPanel/2138, 80*GameUI.hOfPanel/1324, Image.SCALE_AREA_AVERAGING));
		}
		
		
		this.setLayout(null);
		msBlack=new Font("微软雅黑",Font.BOLD,28*GameUI.wOfPanel/2138);
		//menuButton=new JButton("菜单");
		//menuButton.setFont(new Font("微软雅黑",Font.PLAIN,36*GameUI.wOfPanel/2138));
		playerLabel=new JLabel();
		playerLabel.setHorizontalAlignment(SwingConstants.LEFT);
		playerLabel.setFont(msBlack);
		playerLabel.setBackground(new Color(255, 255, 0,80));
		playerLabel.setOpaque(true);
		buyFighterLabel=new JLabel("购买新士兵：",SwingConstants.CENTER);
		buyFighterLabel.setFont(msBlack);
		spearMan=new JButton("长矛兵");
		spearMan.setFont(msBlack);
		spearMan.setActionCommand("s");
		spearMan.addActionListener(new BuyFighterListener());
		swordMan=new JButton("持剑兵");
		swordMan.setFont(msBlack);
		swordMan.setActionCommand("w");
		swordMan.addActionListener(new BuyFighterListener());
		axeMan=new JButton("斧兵");
		axeMan.setFont(msBlack);
		axeMan.setActionCommand("a");
		axeMan.addActionListener(new BuyFighterListener());
		endThisRound=new JButton("开始下一轮");
		endThisRound.setFont(msBlack);
		endThisRound.addActionListener(new EndThisRoundListener());
		currentPlayer=1;
		xClicked=-1;
		yClicked=-1;
		fighterImages=new HashMap<String,Image>();
		validClickPosition=false;
		buyFighterClicked=false;
		fighterChosen=null;
		xChosenFighter=-1;
		yChosenFighter=-1;
		fighterStatusLabel=new JLabel();
		fighterStatusLabel.setFont(msBlack);
		upgradeButton=new JButton("升级");
		upgradeButton.setFont(msBlack);
		upgradeButton.addActionListener(new UpgradeFighterListener());
		moveButton=new JButton("移动");
		moveButton.setFont(msBlack);
		moveButton.addActionListener(new MoveFighterListener());
		occupyButton=new JButton("占领");
		occupyButton.setFont(msBlack);
		occupyButton.addActionListener(new OccupyFighterListener());
		typeInChinese=new HashMap<String,String>();
		typeInChinese.put("s", "矛");
		typeInChinese.put("w", "剑");
		typeInChinese.put("a", "斧");
		moving=false;
		occupying=false;
		directArrows=new HashMap<String,ImageIcon>();
		diagonalArrows=new HashMap<String,ImageIcon>();
		try{
			directArrows.put("north60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/north60.png")));
			directArrows.put("north30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/north30.png")));
			north=new JLabel();
			north.setName("north");
			north.addMouseListener(new ArrowLabelListener());
			directArrows.put("south60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/south60.png")));
			directArrows.put("south30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/south30.png")));
			south=new JLabel();
			south.setName("south");
			south.addMouseListener(new ArrowLabelListener());
			directArrows.put("east60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/east60.png")));
			directArrows.put("east30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/east30.png")));
			east=new JLabel();
			east.setName("east");
			east.addMouseListener(new ArrowLabelListener());
			directArrows.put("west60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/west60.png")));
			directArrows.put("west30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/west30.png")));
			west=new JLabel();
			west.setName("west");
			west.addMouseListener(new ArrowLabelListener());
			diagonalArrows.put("west60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/southwest60.png")));
			diagonalArrows.put("west30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/southwest30.png")));
			diagonalArrows.put("north60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/northwest60.png")));
			diagonalArrows.put("north30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/northwest30.png")));
			diagonalArrows.put("east60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/northeast60.png")));
			diagonalArrows.put("east30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/northeast30.png")));
			diagonalArrows.put("south60",new ImageIcon(GameUI.class.getClassLoader().getResource("image/southeast60.png")));
			diagonalArrows.put("south30",new ImageIcon(GameUI.class.getClassLoader().getResource("image/southeast30.png")));
			gameBackground=ImageIO.read(GameUI.class.getClassLoader().getResource("image/gameBackground.png"));
		}catch(IOException e){
			e.printStackTrace();
		}
		cancelOccupy=new JButton("取消");
		cancelOccupy.setFont(new Font("微软雅黑", Font.BOLD, 24*GameUI.wOfPanel/2138));
		cancelOccupy.setMargin(new Insets(0, 0, 0, 0));
		cancelOccupy.addActionListener(new cancelOccupyListener());
		winner=0;
		gameOver=false;
		exit=new JButton("退出本局游戏");
		exit.setFont(msBlack);
		exit.addActionListener(new ExitGameListener());
		isAI=isAIPlayer;
		fighterTypes=new String[]{"s","w","a"};
		directions=new String[]{"e","s","w","n"};
		random=new Random();
		otherFighter=null;
	}
	
	public void startGame(){
		//mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		wPanel=this.getWidth();
		hPanel=this.getHeight();
		frameHeight=mainFrame.getHeight()-hPanel;
		hSection=(hPanel-80*hPanel/1324)/heightOfMap;
		if(widthOfMap>0.8*heightOfMap){
			wSection=(wPanel*3/4-80*wPanel/2138)/widthOfMap;
		}
		else wSection=hSection;
		cursorSize=Toolkit.getDefaultToolkit().getBestCursorSize(wSection, (int)(hSection*1.4));
		
		try{
			fighterImages.put("s", ImageIO.read(GameUI.class.getClassLoader().getResource("image/s.png")).getScaledInstance(wSection, (int)(hSection*1.4), Image.SCALE_AREA_AVERAGING));
			fighterImages.put("w", ImageIO.read(GameUI.class.getClassLoader().getResource("image/w.png")).getScaledInstance(wSection, (int)(hSection*1.4), Image.SCALE_AREA_AVERAGING));
			fighterImages.put("a", ImageIO.read(GameUI.class.getClassLoader().getResource("image/a.png")).getScaledInstance(wSection, (int)(hSection*1.4), Image.SCALE_AREA_AVERAGING));
			spearImage=fighterImages.get("s").getScaledInstance(cursorSize.width, cursorSize.height, Image.SCALE_AREA_AVERAGING);
			swordImage=fighterImages.get("w").getScaledInstance(cursorSize.width, cursorSize.height, Image.SCALE_AREA_AVERAGING);
			axeImage=fighterImages.get("a").getScaledInstance(cursorSize.width, cursorSize.height, Image.SCALE_AREA_AVERAGING);
		}catch(IOException e){
			e.printStackTrace();
		}
		cursorHotspot=new Point(cursorSize.width/2, cursorSize.height*91/100);
		spearCursor=Toolkit.getDefaultToolkit().createCustomCursor(spearImage,cursorHotspot, "s");
		swordCursor=Toolkit.getDefaultToolkit().createCustomCursor(swordImage, cursorHotspot, "w");
		axeCursor=Toolkit.getDefaultToolkit().createCustomCursor(axeImage, cursorHotspot, "a");
		try{
			moveCursor=Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(GameUI.class.getClassLoader().getResource("image/move.png")).getScaledInstance(cursorSize.width, cursorSize.height, Image.SCALE_AREA_AVERAGING), new Point(cursorSize.width/2, cursorSize.height-1),"m");
			//occupyCursor=Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(GameUI.class.getClassLoader().getResource("image/occupy.png")).getScaledInstance(cursorSize.width, cursorSize.height, Image.SCALE_AREA_AVERAGING), new Point(cursorSize.width/2, cursorSize.height/2),"o");
		}catch(IOException e){
			e.printStackTrace();
		}
		RepaintManager.currentManager(this).addDirtyRegion(this, 40*wPanel/2138, 40*hPanel/1324,wPanel*3/4-80*wPanel/2138 , hPanel-80*hPanel/1324);
		RepaintManager.currentManager(this).addDirtyRegion(this, wPanel*3/4, 280*hPanel/1324, 200*wPanel/2138, 80*hPanel/1324);
		//long time=System.nanoTime();
		//System.out.println((System.nanoTime()-time)/1000000);
		
		//add(menuButton);
		//menuButton.setBounds(wPanel-340*wPanel/2138, 40*hPanel/1324, 300*wPanel/2138, 80*hPanel/1324);
		add(exit);
		exit.setBounds(wPanel-440*wPanel/2138, hPanel-120*hPanel/1324, 400*wPanel/2138, 80*hPanel/1324);
		add(playerLabel);
		playerLabel.setBounds(wPanel*3/4+20*wPanel/2138, 160*hPanel/1324, wPanel/4-40*wPanel/2138, 80*hPanel/1324);
		//repaint();
		
				playerLabel.setText("    "+((isAI[currentPlayer-1]>0)?"AI-":"玩家")+currentPlayer+"    金钱："+control.moneyOfPlayers[currentPlayer-1]);
				playerLabel.setIcon(playerIcon[currentPlayer-1]);
				add(buyFighterLabel);
				buyFighterLabel.setBounds(wPanel*3/4, 280*hPanel/1324, 200*wPanel/2138, 80*hPanel/1324);
				add(spearMan);
				spearMan.setBounds(wPanel*3/4+20*wPanel/2138, 360*hPanel/1324, 120*wPanel/2138, 60*hPanel/1324);
				add(swordMan);
				swordMan.setBounds(wPanel*3/4+180*wPanel/2138, 360*hPanel/1324, 120*wPanel/2138, 60*hPanel/1324);
				add(axeMan);
				axeMan.setBounds(wPanel*3/4+340*wPanel/2138, 360*hPanel/1324, 120*wPanel/2138, 60*hPanel/1324);
				add(endThisRound);
				endThisRound.setBounds(wPanel*3/4+240*wPanel/2138,660*hPanel/1324,220*wPanel/2138,60*hPanel/1324);
				refreshBuyFighterButtons();
				
			mouseWatcher=new FrameMouseListener();
			mainFrame.addMouseListener(mouseWatcher);
			mainFrame.addMouseMotionListener(mouseWatcher);
			panelsOfSections=new HashMap<Integer,HashMap<Integer, JPanel>>();
			//JPanel panel;
			/*for(int i=1;i<=widthOfMap;i++){
				panelsOfSections.put(i, new HashMap<Integer,JPanel>());
				for(int j=1;j<=heightOfMap;j++){
					panelsOfSections.get(i).put(j, new JPanel(null));
					panel=panelsOfSections.get(i).get(j);
					panel.setOpaque(false);
					add(panel);
					panel.setBounds(40+(i-1)*wSection, 40+(j-1)*hSection, wSection, hSection);
				}
			}*/
	}
	
	public void exitGame(){
		mainFrame.remove(this);
		mainFrame.add(GameUI.indexPage);
		GameUI.indexPage.setBounds(0,0,GameUI.wOfPanel,GameUI.hOfPanel);
		GameUI.indexPage.removeAll();
		if(winner>0){
			GameUI.indexPage.gameResult.setText(((isAI[winner-1]>0)?"AI-":"玩家")+winner+"获得了胜利！");
		}
		else GameUI.indexPage.gameResult.setText("平局！");
		GameUI.indexPage.add(GameUI.indexPage.gameResult);
		GameUI.indexPage.gameResult.setHorizontalAlignment(JLabel.CENTER);
		GameUI.indexPage.gameResult.setBounds(0, 40*hPanel/1324, wPanel, 100*hPanel/1324);
		GameUI.indexPage.gameStats=new JLabel[numOfPlayers];
		for(int i=0;i<GameUI.indexPage.gameStats.length;i++){
			GameUI.indexPage.gameStats[i]=new JLabel(((isAI[i]>0)?"AI-":"玩家")+(i+1)+"占领的面积："+control.sectionsOccupiedByPlayers.get(i+1).size());
			GameUI.indexPage.gameStats[i].setFont(msBlack);
			GameUI.indexPage.gameStats[i].setHorizontalAlignment(JLabel.CENTER);
			GameUI.indexPage.gameStats[i].setBackground(new Color(255, 255, 0,80));
			GameUI.indexPage.gameStats[i].setOpaque(true);
			GameUI.indexPage.add(GameUI.indexPage.gameStats[i]);
			GameUI.indexPage.gameStats[i].setBounds(0,120*i*hPanel/1324+240*hPanel/1324,wPanel,80*hPanel/1324);
		}
		GameUI.indexPage.add(GameUI.indexPage.anotherOne);
		GameUI.indexPage.anotherOne.setBounds(wPanel/2-340*wPanel/2138,120*numOfPlayers*hPanel/1324+240*hPanel/1324,300*wPanel/2138,80*hPanel/1324);
		GameUI.indexPage.add(GameUI.indexPage.backToIndex);
		GameUI.indexPage.backToIndex.setBounds(wPanel/2+40*wPanel/2138,120*numOfPlayers*hPanel/1324+240*hPanel/1324,300*wPanel/2138,80*hPanel/1324);
		GameUI.indexPage.atResultPage=true;
		GameUI.indexPage.atCreditsPage=false;
		GameUI.indexPage.atIndexPage=false;
		GameUI.indexPage.atSettingPage=false;
		mainFrame.paintComponents(mainFrame.getGraphics());
	}
	
	public void randomAI(int grade){
		Section section;
		double operation;
		int fighter;
		int[] availableFighters;
		int highestLevel=1;
		ArrayList<Section> listOfSections=new ArrayList<Section>();
		listOfSections.addAll(control.sectionsOccupiedByPlayers.get(currentPlayer));
		if(rounds==control.roundsRemaining){
			section=listOfSections.get(random.nextInt(listOfSections.size()));
			control.purchase(currentPlayer, fighterTypes[random.nextInt(3)], section.x, section.y);
		}
		availableFighters=control.fightersOfPlayers.get(currentPlayer).values().stream().mapToInt(s->s.fighterNum).toArray();
		if(grade==1){
			while(control.moneyOfPlayers[currentPlayer-1]>20){
				availableFighters=control.fightersOfPlayers.get(currentPlayer).values().stream().mapToInt(s->s.fighterNum).toArray();
				operation=random.nextDouble();
				if(availableFighters.length==0){
					for(int i=0;i<10;i++){
						section=listOfSections.get(random.nextInt(listOfSections.size()));
						if(control.purchaseAllowed(currentPlayer, "s")){
							control.purchase(currentPlayer, fighterTypes[random.nextInt(3)], section.x, section.y);
							availableFighters=control.fightersOfPlayers.get(currentPlayer).values().stream().mapToInt(s->s.fighterNum).toArray();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									paintImmediately(0, 0, wPanel, hPanel);
								}
							}).start();
							break;
						}
					}
				}
				else{
					fighter=availableFighters[random.nextInt(availableFighters.length)];
					//Fighter z=control.fightersOfPlayers.get(currentPlayer).get(fighter);
					if(operation<0.15){
						if(control.upgradeAllowed(currentPlayer,fighter)){
							control.upgrade(currentPlayer, fighter);
						}
					}
					else if(operation<0.3){
						if(control.purchaseAllowed(currentPlayer, "s")){
							section=listOfSections.get(random.nextInt(listOfSections.size()));
							if(control.availableForNewFighter(currentPlayer, section.x, section.y)){
								control.purchase(currentPlayer, fighterTypes[random.nextInt(3)], section.x, section.y);
								availableFighters=control.fightersOfPlayers.get(currentPlayer).values().stream().mapToInt(s->s.fighterNum).toArray();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										paintImmediately(0, 0, wPanel, hPanel);
									}
								}).start();
							}
						}
					}
					else if(operation<0.7){
						section=listOfSections.get(random.nextInt(listOfSections.size()));
						if(control.moveAllowed(currentPlayer, fighter, section.x, section.y)){
							control.move(currentPlayer, fighter, section.x, section.y);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									paintImmediately(0, 0, wPanel, hPanel);
								}
							}).start();
						}
					}
					else if(control.moneyOfPlayers[currentPlayer-1]>=occupyCost){
						control.refresh(currentPlayer, fighter);
						Object[] dirs=control.fightersOfPlayers.get(currentPlayer).get(fighter).occupyDirections.toArray();
						if(dirs.length>0){
							//control.refresh(currentPlayer, fighter);
							control.occupy(currentPlayer, fighter,(String)dirs[random.nextInt(dirs.length)]);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									paintImmediately(0, 0, wPanel, hPanel);
								}
							}).start();
						}
					}
				}
			}
		}
		if(grade==2){
			while(control.moneyOfPlayers[currentPlayer-1]>20){
				operation=random.nextDouble();
				if(availableFighters.length==0){
					for(int i=0;i<10;i++){
						section=listOfSections.get(random.nextInt(listOfSections.size()));
						if(control.purchaseAllowed(currentPlayer, "s")){
							control.purchase(currentPlayer, fighterTypes[random.nextInt(3)], section.x, section.y);
							availableFighters=control.fightersOfPlayers.get(currentPlayer).values().stream().mapToInt(s->s.fighterNum).toArray();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									paintImmediately(0, 0, wPanel, hPanel);
								}
							}).start();
							break;
						}
					}
				}
				else{
					fighter=availableFighters[random.nextInt(availableFighters.length)];
					Fighter z=control.fightersOfPlayers.get(currentPlayer).get(fighter);
					
					
					if(operation<0.15){
						if(control.upgradeAllowed(currentPlayer,fighter)){
							control.upgrade(currentPlayer, fighter);
							highestLevel=Math.max(highestLevel,z.level);
						}
					}
					else if(operation<0.3){
						if(control.purchaseAllowed(currentPlayer, "s")){
							section=listOfSections.get(random.nextInt(listOfSections.size()));
							if(control.availableForNewFighter(currentPlayer, section.x, section.y)){
								control.purchase(currentPlayer, fighterTypes[random.nextInt(3)], section.x, section.y);
								availableFighters=control.fightersOfPlayers.get(currentPlayer).values().stream().mapToInt(s->s.fighterNum).toArray();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										paintImmediately(0, 0, wPanel, hPanel);
									}
								}).start();
							}
						}
					}
					else if(operation<0.5){
						section=listOfSections.get(random.nextInt(listOfSections.size()));
						if(control.moveAllowed(currentPlayer, fighter, section.x, section.y)){
							control.move(currentPlayer, fighter, section.x, section.y);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									paintImmediately(0, 0, wPanel, hPanel);
								}
							}).start();
						}
					}
					else if(control.moneyOfPlayers[currentPlayer-1]>=occupyCost){
						if(z.level==1&&z.level<highestLevel){
							fighter=availableFighters[random.nextInt(availableFighters.length)];
							z=control.fightersOfPlayers.get(currentPlayer).get(fighter);
						}
						if(z.level==1&&z.level<highestLevel){
							fighter=availableFighters[random.nextInt(availableFighters.length)];
							z=control.fightersOfPlayers.get(currentPlayer).get(fighter);
						}
						Fighter zz=z;
						control.refresh(currentPlayer, fighter);
						boolean occupyFinished=false;
						Object[] dirs=control.fightersOfPlayers.get(currentPlayer).get(fighter).occupyDirections.toArray();
						Object[] enemySects=control.fightersOfPlayers.values().stream().map(m->m.values()).flatMap(Collection::stream).filter(f->f.player!=currentPlayer).map(f->sectionMap.get(f.x).get(f.y)).toArray();
						if(enemySects.length>0){
							Object[] greatSects=z.moveToSections.stream().filter(s->control.getOccupyRange(zz.type, "a", s.x, s.y).stream().filter(a->Arrays.asList(enemySects).contains(a)).count()>0).toArray();
							if(greatSects.length>0){
								for(int j=0;j<greatSects.length;j++){
									if(control.moneyOfPlayers[currentPlayer-1]>=occupyCost+control.movePathLength(currentPlayer, fighter, ((Section)greatSects[j]).x, ((Section)greatSects[j]).y)){
										if(control.getOccupyRange(z.type, "e", ((Section)greatSects[j]).x, ((Section)greatSects[j]).y).stream().filter(a->Arrays.asList(enemySects).contains(a)).count()>0&&control.occupyAllowed(currentPlayer, fighter, "e")){
											control.move(currentPlayer, fighter, ((Section)greatSects[j]).x, ((Section)greatSects[j]).y);
											control.occupy(currentPlayer, fighter, "e");
											occupyFinished=true;
											break;
										}
										if(control.getOccupyRange(z.type, "s", ((Section)greatSects[j]).x, ((Section)greatSects[j]).y).stream().filter(a->Arrays.asList(enemySects).contains(a)).count()>0&&control.occupyAllowed(currentPlayer, fighter, "s")){
											control.move(currentPlayer, fighter, ((Section)greatSects[j]).x, ((Section)greatSects[j]).y);
											control.occupy(currentPlayer, fighter, "s");
											occupyFinished=true;
											break;
										}
										if(control.getOccupyRange(z.type, "w", ((Section)greatSects[j]).x, ((Section)greatSects[j]).y).stream().filter(a->Arrays.asList(enemySects).contains(a)).count()>0&&control.occupyAllowed(currentPlayer, fighter, "w")){
											control.move(currentPlayer, fighter, ((Section)greatSects[j]).x, ((Section)greatSects[j]).y);
											control.occupy(currentPlayer, fighter, "w");
											occupyFinished=true;
											break;
										}
										if(control.getOccupyRange(z.type, "n", ((Section)greatSects[j]).x, ((Section)greatSects[j]).y).stream().filter(a->Arrays.asList(enemySects).contains(a)).count()>0&&control.occupyAllowed(currentPlayer, fighter, "n")){
											control.move(currentPlayer, fighter, ((Section)greatSects[j]).x, ((Section)greatSects[j]).y);
											control.occupy(currentPlayer, fighter, "n");
											occupyFinished=true;
											break;
										}
									}
								}
							}
						}
						if(!occupyFinished){
							if(dirs.length>0){
								control.occupy(currentPlayer, fighter,(String)dirs[random.nextInt(dirs.length)]);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								paintImmediately(0, 0, wPanel, hPanel);
							}
						}).start();
					}
				}
			}
		}
		playerLabel.setText("    "+((isAI[currentPlayer-1]>0)?"AI-":"玩家")+currentPlayer+"    金钱："+control.moneyOfPlayers[currentPlayer-1]);
		/*System.out.println("painted");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				paintImmediately(0, 0, wPanel, hPanel);
			}
		}).start();*/
		endThisRound.getActionListeners()[0].actionPerformed(null);
	}
	
	class EndThisRoundListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(control.sectionsOccupiedByPlayers.get(currentPlayer).size()==widthOfMap*heightOfMap){
				winner=currentPlayer;
				gameOver=true;
				repaint();
				return;
			}
			if(currentPlayer==numOfPlayers){
				currentPlayer=1;
				control.roundsRemaining--;
				for(int i=1;i<=numOfPlayers;i++){
					control.moneyOfPlayers[i-1]+=incomePerSection*(control.sectionsOccupiedByPlayers.get(i).size()+control.fightersOfPlayers.get(i).values().stream().mapToInt(s->s.level).sum());
				}
			}
			else currentPlayer++;
			if(control.roundsRemaining==0){
				winner=control.getWinner();
				gameOver=true;
				exitGame();
			}
			else{
				
			removeAll();
			//add(menuButton);
			//menuButton.setBounds(wPanel-340*wPanel/2138, 40*hPanel/1324, 300*wPanel/2138, 80*hPanel/1324);
			add(exit);
			exit.setBounds(wPanel-440*wPanel/2138, hPanel-120*hPanel/1324, 400*wPanel/2138, 80*hPanel/1324);
			//playerLabel.setText("    玩家"+currentPlayer+"目前金钱："+control.moneyOfPlayers[currentPlayer-1]);
			playerLabel.setIcon(playerIcon[currentPlayer-1]);
			add(playerLabel);
			if(isAI[currentPlayer-1]>0){
				repaint();
				randomAI(isAI[currentPlayer-1]);
				return;
			}
			add(buyFighterLabel);
			buyFighterLabel.setVisible(true);
			buyFighterLabel.setBounds(wPanel*3/4, 280*hPanel/1324, 200*wPanel/2138, 80*hPanel/1324);
			add(spearMan);
			spearMan.setBounds(wPanel*3/4+20*wPanel/2138, 360*hPanel/1324, 120*wPanel/2138, 60*hPanel/1324);
			add(swordMan);
			swordMan.setBounds(wPanel*3/4+180*wPanel/2138, 360*hPanel/1324, 120*wPanel/2138, 60*hPanel/1324);
			add(axeMan);
			axeMan.setBounds(wPanel*3/4+340*wPanel/2138, 360*hPanel/1324, 120*wPanel/2138, 60*hPanel/1324);
			add(endThisRound);
			endThisRound.setBounds(wPanel*3/4+240*wPanel/2138,660*hPanel/1324,220*wPanel/2138,60*hPanel/1324);
			fighterChosen=null;
			xChosenFighter=-1;
			yChosenFighter=-1;
			refreshBuyFighterButtons();
			repaint();
			
			}
		}
	}
	
	class BuyFighterListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(otherFighter!=null){
				otherFighter=null;
				remove(fighterStatusLabel);
				repaint();
			}
			switch(e.getActionCommand()){
				case "s":getParent().setCursor(spearCursor);
				break;
				case "w":getParent().setCursor(swordCursor);
				break;
				case "a":getParent().setCursor(axeCursor);
			}
			buyFighterClicked=true;
			fighterBought=e.getActionCommand();
						
		}
	}
	
	class UpgradeFighterListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			control.upgrade(currentPlayer, fighterChosen.fighterNum);
			getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			refreshBuyFighterButtons();
			repaint();
		}
	}
	
	class MoveFighterListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			moving=true;
			if(occupying){
				occupying=false;
				remove(west);
				remove(east);
				remove(north);
				remove(south);
				remove(cancelOccupy);
				repaint();
			}
			getParent().setCursor(moveCursor);
		}
	}
	
	class OccupyFighterListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			occupying=true;
			moving=false;
			//getParent().setCursor(occupyCursor);
			getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			renewArrowButtons();
			
			repaint();
		}
	}
	
	class ArrowLabelListener extends MouseAdapter{
		@Override
		public void mouseEntered(MouseEvent e) {
			if(occupying){
				JLabel label=(JLabel)e.getSource();
				if(fighterChosen.type=="w"){
					label.setIcon(diagonalArrows.get(label.getName()+"60"));
					label.repaint();
					label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else{
					label.setIcon(directArrows.get(label.getName()+"60"));
					label.repaint();
					label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			if(occupying){
				JLabel label=(JLabel)e.getSource();
				if(fighterChosen.type=="w"){
					label.setIcon(diagonalArrows.get(label.getName()+"30"));
					label.repaint();
					label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				else{
					label.setIcon(directArrows.get(label.getName()+"30"));
					label.repaint();
					label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(occupying){
				control.occupy(currentPlayer, fighterChosen.fighterNum, ((JLabel)e.getSource()).getName().substring(0, 1));
				remove(west);
				remove(east);
				remove(north);
				remove(south);
				remove(cancelOccupy);
				occupying=false;
				refreshBuyFighterButtons();
				repaint();
			}
		}
	}
	
	class cancelOccupyListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			occupying=false;
			remove(west);
			remove(east);
			remove(north);
			remove(south);
			remove(cancelOccupy);
			repaint();
		}
	}
	
	
	class ExitGameListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(rounds-control.roundsRemaining<1) winner=0;
			else winner=control.getWinner();
			exitGame();
		}
	}
	
	
	public void refreshBuyFighterButtons(){
		if(control.purchaseAllowed(currentPlayer, "s")) spearMan.setEnabled(true);
		else spearMan.setEnabled(false);
		if(control.purchaseAllowed(currentPlayer, "w")) swordMan.setEnabled(true);
		else swordMan.setEnabled(false);
		if(control.purchaseAllowed(currentPlayer, "a")) axeMan.setEnabled(true);
		else axeMan.setEnabled(false);
		if(fighterChosen!=null){
			if(control.upgradeAllowed(currentPlayer, fighterChosen.fighterNum)) upgradeButton.setEnabled(true);
			else upgradeButton.setEnabled(false);
			if(control.moneyOfPlayers[currentPlayer-1]>=occupyCost) occupyButton.setEnabled(true);
			else occupyButton.setEnabled(false);
			if(control.moneyOfPlayers[currentPlayer-1]>=1) moveButton.setEnabled(true);
			else moveButton.setEnabled(false);
			fighterStatusLabel.setText(typeInChinese.get(fighterChosen.type)+"兵    等级"+fighterChosen.level);
		}
		playerLabel.setText("    "+((isAI[currentPlayer-1]>0)?"AI-":"玩家")+currentPlayer+"    金钱："+control.moneyOfPlayers[currentPlayer-1]);
	}
	
	public void renewArrowButtons(){
		int x=40*wPanel/2138+(fighterChosen.x-1)*wSection;
		int y=40*hPanel/1324+(fighterChosen.y-1)*hSection;
		remove(east);
		remove(west);
		remove(south);
		remove(north);
		if(fighterChosen.type=="w"){
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "e")){
			east.setIcon(diagonalArrows.get("east30"));
			add(east);
			east.setBounds(x+wSection,y-160*hPanel/1324,160*wPanel/2138,160*hPanel/1324);
			}
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "n")){
			north.setIcon(diagonalArrows.get("north30"));
			add(north);
			north.setBounds(x-160*wPanel/2138,y-160*hPanel/1324,160*wPanel/2138,160*hPanel/1324);
			}
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "s")){
			south.setIcon(diagonalArrows.get("south30"));
			add(south);
			south.setBounds(x+wSection,y+hSection,160*wPanel/2138,160*hPanel/1324);
			}
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "w")){
			west.setIcon(diagonalArrows.get("west30"));
			add(west);
			west.setBounds(x-160*wPanel/2138,y+hSection,160*wPanel/2138,160*hPanel/1324);
			}
		}
		else{
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "w")){
			add(west);
			west.setIcon(directArrows.get("west30"));
			west.setBounds(x-180*wPanel/2138,y+hSection/2-80*hPanel/1324,160*wPanel/2138,160*hPanel/1324);
			}
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "e")){
			add(east);
			east.setIcon(directArrows.get("east30"));
			east.setBounds(x+20*wPanel/2138+wSection,y+hSection/2-80*hPanel/1324,160*wPanel/2138,160*hPanel/1324);
			}
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "s")){
			add(south);
			south.setIcon(directArrows.get("south30"));
			south.setBounds(x+wSection/2-80*wPanel/2138,y+hSection+20*hPanel/1324,160*wPanel/2138,160*hPanel/1324);
			}
			if(control.occupyAllowed(currentPlayer, fighterChosen.fighterNum, "n")){
			add(north);
			north.setIcon(directArrows.get("north30"));
			north.setBounds(x+wSection/2-80*wPanel/2138,y-180*hPanel/1324,160*wPanel/2138,160*hPanel/1324);
			}
		}
		add(cancelOccupy);
		if(wSection>70*wPanel/2138&&hSection>35*hPanel/1324){
			if(widthOfMap-fighterChosen.x>5){
				if(fighterChosen.type=="w") cancelOccupy.setBounds(x+wSection*3/2, y, wSection, hSection);
				else cancelOccupy.setBounds(x+wSection*3/2, y+hSection*3/2, wSection, hSection);
			}
			else{
				if(fighterChosen.type=="w") cancelOccupy.setBounds(x-wSection*3/2, y, wSection, hSection);
				else cancelOccupy.setBounds(x-wSection*3/2, y+hSection*3/2, wSection, hSection);
			}
		}
		else{
			if(widthOfMap-fighterChosen.x>5){
				if(fighterChosen.type=="w") cancelOccupy.setBounds(x+wSection*3/2, y, 70*wPanel/2138, 35*hPanel/1324);
				else cancelOccupy.setBounds(x+wSection*3/2, y+hSection*3/2, 70*wPanel/2138, 35*hPanel/1324);
			}
			else{
				if(fighterChosen.type=="w") cancelOccupy.setBounds(x-wSection*3/2, y, 70*wPanel/2138, 35*hPanel/1324);
				else cancelOccupy.setBounds(x-wSection*3/2, y+hSection*3/2, 70*wPanel/2138, 35*hPanel/1324);
			}
			cancelOccupy.setBackground(new Color(200, 200, 200, 125));
		}
		//cancelOccupy.setBackground(new Color(200, 200, 200, 255));
	}
	
	public void drawMap(BufferedImage content){
		Graphics2D g2d=content.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		for(int i=1;i<=widthOfMap;i++){
			for(int j=1;j<heightOfMap;j++){
				if(sectionMap.get(i).get(j)!=null){
					g2d.drawImage(sectionImages[sectionMap.get(i).get(j).status-1], (i-1)*wSection+40*wPanel/2138, (j-1)*hSection+40*hPanel/1324, wSection, hSection, null);
				}
			}
		}
		for(int i=1;i<=widthOfMap;i++){
			for(int j=1;j<heightOfMap;j++){
				if(sectionMap.get(i).get(j).onSect!=null){
					g2d.drawImage(fighterImages.get(sectionMap.get(i).get(j).onSect.type), (i-1)*wSection+40*wPanel/2138, (int)((j-1.4)*hSection+40*hPanel/1324), wSection, (int)(hSection*1.4), null);
				}
			}
		}
		if(fighterChosen!=null){
			RescaleOp op=new RescaleOp(new float[]{1.7f,1.7f,1.7f,1.7f}, new float[]{0,0,0,0}, null);
			g2d.drawImage(op.filter(content.getSubimage((fighterChosen.x-1)*wSection+40*wPanel/2138, (fighterChosen.y-1)*hSection+40*hPanel/1324, wSection, hSection), null),  (fighterChosen.x-1)*wSection+40*wPanel/2138, (fighterChosen.y-1)*hSection+40*hPanel/1324, wSection, hSection,null);
		}
		if(otherFighter!=null){
			RescaleOp op2=new RescaleOp(new float[]{1.7f,1.7f,1.7f,1.7f}, new float[]{0,0,0,0}, null);
			g2d.drawImage(op2.filter(content.getSubimage((otherFighter.x-1)*wSection+40*wPanel/2138, (otherFighter.y-1)*hSection+40*hPanel/1324, wSection, hSection), null),  (otherFighter.x-1)*wSection+40*wPanel/2138, (otherFighter.y-1)*hSection+40*hPanel/1324, wSection, hSection,null);
		}
	}
	
	public boolean drawBackground(BufferedImage content){
		Graphics2D g2d=(Graphics2D)content.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(gameBackground, 0, 0,wPanel,hPanel, null);
		return true;
	}
	
	@Override
	public void paint(Graphics g){
		BufferedImage content=new BufferedImage(wPanel, hPanel, BufferedImage.TYPE_4BYTE_ABGR);
		super.setOpaque(false);
		drawBackground(content);
		drawMap(content);
		g.drawImage(content, 0, 0, null);
		super.paint(g);
	}
	
	
	
	class FrameMouseListener extends MouseInputAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			if(isAI[currentPlayer-1]>0) return;
			if(otherFighter!=null){
				otherFighter=null;
				remove(fighterStatusLabel);
			}
			xClicked=e.getX();
			yClicked=e.getY()-frameHeight;
			int xx,yy;
			if(moving&&toMapXX(xClicked)>0&&toMapYY(yClicked)>0){
				
				xx=toMapXX(xClicked);
				yy=toMapYY(yClicked);
				if(control.moveAllowed(currentPlayer, fighterChosen.fighterNum, xx, yy)){
					control.moneyOfPlayers[currentPlayer-1]-=control.movePathLength(currentPlayer, fighterChosen.fighterNum, xx, yy);
					control.move(currentPlayer, fighterChosen.fighterNum, xx, yy);
					getParent().setCursor(Cursor.getDefaultCursor());
					xChosenFighter=xx;
					yChosenFighter=yy;
					refreshBuyFighterButtons();
					repaint();
					moving=false;
					return;
				}
				else{
					getParent().setCursor(Cursor.getDefaultCursor());
					moving=false;
					return;
				}
			}
			else if(moving){
				getParent().setCursor(Cursor.getDefaultCursor());
				moving=false;
				return;
			}
			if(occupying){
				if(toMapXX(xClicked)<0||toMapYY(yClicked)<0){
					occupying=false;
					remove(west);
					remove(east);
					remove(north);
					remove(south);
					remove(cancelOccupy);
					repaint();
				}
				return;
			}
			if(buyFighterClicked){
				validClickPosition=true;
				//super.mousePressed(e);
				if(xClicked>=40*wPanel/2138+90*cursorSize.width/1024&&xClicked<=40*wPanel/2138+widthOfMap*wSection-90*cursorSize.width/1024&&yClicked>=40*hPanel/1324+cursorSize.height*370/1024&&yClicked<=hPanel-40*hPanel/1324+cursorSize.height*130/1024){
					if((xClicked-40*wPanel/2138)%wSection>=90*cursorSize.width/1024&&(xClicked-40*wPanel/2138)%wSection<=wSection-90*cursorSize.width/1024) xx=(xClicked-40*wPanel/2138)/wSection+1;
					else xx=-1;
					if((yClicked-40*hPanel/1324)%hSection<=130*cursorSize.height/1024||(yClicked-40*hPanel/1324)%hSection>=370*cursorSize.height/1024) yy=(yClicked-40*hPanel/1324)/hSection+1;
					else yy=-1;
					if(xx<1||yy<1||xx>widthOfMap||yy>heightOfMap) ;
					else if(control.availableForNewFighter(currentPlayer, xx, yy)){
						control.purchase(currentPlayer, fighterBought, xx, yy);
						getParent().setCursor(Cursor.getDefaultCursor());
						fighterChosen=sectionMap.get(xx).get(yy).onSect;
						xChosenFighter=xx;
						yChosenFighter=yy;
						fighterStatusLabel.setText(typeInChinese.get(fighterChosen.type)+"兵    等级"+fighterChosen.level);
						add(fighterStatusLabel);
						fighterStatusLabel.setBounds(wPanel*3/4+20*wPanel/2138, 460*hPanel/1324, wPanel/4-30*wPanel/2138, 60*hPanel/1324);
						add(upgradeButton);
						upgradeButton.setBounds(wPanel*3/4+260*wPanel/2138,460*hPanel/1324,200*wPanel/2138,60*hPanel/1324);
						add(moveButton);
						moveButton.setBounds(wPanel*3/4+20*wPanel/2138, 560*hPanel/1324, 200*wPanel/2138, 60*hPanel/1324);
						add(occupyButton);
						occupyButton.setBounds(wPanel*3/4+260*wPanel/2138, 560*hPanel/1324, 200*wPanel/2138, 60*hPanel/1324);
						refreshBuyFighterButtons();
						repaint();
						validClickPosition=false;
						buyFighterClicked=false;
					}
					else{
						getParent().setCursor(Cursor.getDefaultCursor());
						validClickPosition=false;
						buyFighterClicked=false;
					}
				}
				else{
					getParent().setCursor(Cursor.getDefaultCursor());
					validClickPosition=false;
					buyFighterClicked=false;
				}
			}
			else{
				if(toMapXX(xClicked)>0&&toMapYY(yClicked)>0/*&&xOffSet(xClicked)>=wSection/4&&xOffSet(xClicked)<=wSection*3/4*/){
					xx=toMapXX(xClicked);
					yy=toMapYY(yClicked);
					Fighter chosen=control.sectionMap.get(xx).get(yy).onSect;
					if(chosen!=null){
						if(chosen.player==currentPlayer){
							fighterStatusLabel.setText(typeInChinese.get(chosen.type)+"兵    等级"+chosen.level);
							if(fighterChosen==null){
								add(fighterStatusLabel);
								fighterStatusLabel.setBounds(wPanel*3/4+20*wPanel/2138, 460*hPanel/1324, wPanel/4-30*wPanel/2138, 60*hPanel/1324);
								add(upgradeButton);
								upgradeButton.setBounds(wPanel*3/4+240*wPanel/2138,460*hPanel/1324,220*wPanel/2138,60*hPanel/1324);
								add(moveButton);
								moveButton.setBounds(wPanel*3/4+20*wPanel/2138, 560*hPanel/1324, 200*wPanel/2138, 60*hPanel/1324);
								add(occupyButton);
								occupyButton.setBounds(wPanel*3/4+260*wPanel/2138, 560*hPanel/1324, 200*wPanel/2138, 60*hPanel/1324);
							}
							fighterChosen=chosen;
							xChosenFighter=xx;
							yChosenFighter=yy;
							if(occupying){
								renewArrowButtons();
							}
						}
						else{
							remove(upgradeButton);
							remove(occupyButton);
							remove(moveButton);
							add(fighterStatusLabel);
							fighterStatusLabel.setText((isAI[chosen.player-1]==0?"玩家":"AI-")+chosen.player+" "+typeInChinese.get(chosen.type)+"兵    等级"+chosen.level);
							otherFighter=chosen;
							fighterChosen=null;
						}
						
					
						refreshBuyFighterButtons();
					}
					if(chosen==null){
						xChosenFighter=0;
						yChosenFighter=0;
					}
					repaint();
				}
				else{
					if(fighterChosen!=null){
						fighterChosen=null;
						xChosenFighter=0;
						yChosenFighter=0;
						remove(fighterStatusLabel);
						remove(upgradeButton);
						remove(moveButton);
						remove(occupyButton);
					}
					repaint();
				}
			}
		}
	}
	
	public int toMapXX(int x){
		if(x<40*wPanel/2138||x>=40*wPanel/2138+widthOfMap*wSection){
			return -1;
		}
		else return (x-40*wPanel/2138)/wSection+1;
	}
	public int toMapYY(int y){
		if(y<40*hPanel/1324||y>=40*hPanel/1324+heightOfMap*hSection){
			return -1;
		}
		else return (y-40*hPanel/1324)/hSection+1;
	}
	public int xOffSet(int x){
		if(x<40*wPanel/2138||x>=40*wPanel/2138+widthOfMap*wSection){
			return -1;
		}
		else return (x-40*wPanel/2138)%wSection;
	}
	public int yOffSet(int y){
		if(y<40*hPanel/1324||y>=40*hPanel/1324+heightOfMap*hSection){
			return -1;
		}
		else return (y-40*hPanel/1324)%hSection;
	}
	
}

class IndexPage extends JPanel{
	private static final long serialVersionUID = 1L;
	JFrame frame;
	IndexPage thisPage;
	Font msBlack;
	JLabel gameTitle;
	JButton multiplayerMode;
	JButton viewCredits;
	JComboBox<Integer> aiPlayersNumBox;
	JLabel aiNumLabel;
	JComboBox<Integer> humanPlayersNumBox;
	JLabel humanNumLabel;
	JLabel aiLevelLabel;
	JComboBox<String> aiLevelBox;
	JButton backToIndex;
	JLabel multiLabel;
	Integer playerNum;
	Integer AIPlayerNum;
	JButton startPlayingTheFxxkingGame;
	JLabel mapSizeLabel;
	JComboBox<String> setMapSizeBox;
	JLabel customizeMapLabel;
	JLabel heightToCustomizeLabel;
	JComboBox<Integer> setWidth;
	JComboBox<Integer> setHeight;
	Integer wMap;
	Integer hMap;
	JLabel gameResult;
	JLabel[] gameStats;
	JButton anotherOne;
	JButton quitButton;
	JLabel loadingGame;
	JLabel youStudio;
	JLabel groupLeader;
	JLabel groupMembers;
	int aiLevel;
	int[] isAI;
	BufferedImage indexBackground;
	BufferedImage creditsBackground;
	BufferedImage settingBackground;
	BufferedImage resultBackground;
	boolean atIndexPage;
	boolean atCreditsPage;
	boolean atSettingPage;
	boolean atResultPage;
	Color creditsColor;
	
	public IndexPage(JFrame mainFrame){
		super();
		frame=mainFrame;
		thisPage=this;
		setLayout(null);
		gameTitle=new JLabel("混沌方块战争");
		gameTitle.setHorizontalAlignment(JLabel.CENTER);
		multiplayerMode=new JButton("进入游戏");
		multiplayerMode.setContentAreaFilled(false);
		multiplayerMode.setBorderPainted(false);
		multiplayerMode.setForeground(new Color(211, 100, 18));
		multiplayerMode.addActionListener(new multiplayerListener());
		viewCredits=new JButton("查看制作团队");
		viewCredits.setBorderPainted(false);
		viewCredits.setBackground(new Color(211, 100, 18));
		viewCredits.addActionListener(new creditsListener());
		humanNumLabel=new JLabel("选择玩家个数");
		//humanNumLabel.setSize(300,80);
		humanPlayersNumBox=new JComboBox<>(new Integer[]{0,1,2,3,4,5,6});
		humanPlayersNumBox.addActionListener(new numOfPlayerListener());
		humanPlayersNumBox.setBackground(new Color(240, 128, 29));
		aiNumLabel=new JLabel("选择AI个数");
		//aiNumLabel.setSize(300,80);
		aiPlayersNumBox=new JComboBox<>(new Integer[]{0,1,2,3,4,5,6});
		aiPlayersNumBox.addActionListener(new numOfAIListener());
		aiPlayersNumBox.setBackground(new Color(240, 128, 29));
		aiLevelLabel=new JLabel("AI级别");
		aiLevelLabel.setBackground(new Color(240, 128, 29,255*7/10));
		aiLevelLabel.setOpaque(true);
		//aiLevelLabel.setSize(200,80);
		aiLevelBox=new JComboBox<>(new String[]{"简单","中等"});
		aiLevelBox.addActionListener(new levelListener());
		aiLevelBox.setBackground(new Color(240, 128, 29));
		multiLabel=new JLabel("新游戏设置");
		multiLabel.setHorizontalAlignment(JLabel.CENTER);
		multiLabel.setForeground(new Color(59,14,139));
		multiLabel.setOpaque(false);
		startPlayingTheFxxkingGame=new JButton("开始游戏");
		startPlayingTheFxxkingGame.addActionListener(new startPlayingTheFxxkingGameListener());
		startPlayingTheFxxkingGame.setBackground(new Color(240, 128, 29));
		backToIndex=translucentButton(new Color(255, 255, 0, 70), "主菜单");
		backToIndex.addActionListener(new backButtonListener());
		mapSizeLabel=new JLabel("选择地图尺寸");
		setMapSizeBox=new JComboBox<String>(new String[]{"小","中","大","自定义"});
		setMapSizeBox.setSelectedItem(null);
		setMapSizeBox.addActionListener(new mapSizeListener());
		setMapSizeBox.setBackground(new Color(240, 128, 29));
		customizeMapLabel=new JLabel("自定义地图宽度：");
		heightToCustomizeLabel=new JLabel("自定义地图高度：");
		setWidth=new JComboBox<Integer>(new Integer[]{10,12,14,16,18,20,22,24,26,28,30,32,34});
		setWidth.setSelectedItem(null);
		setWidth.addActionListener(new widthCustomizedListener());
		setWidth.setBackground(new Color(240, 128, 29));
		setHeight=new JComboBox<Integer>(new Integer[]{10,12,14,16,18,20,22,24,26,28,30});
		setHeight.setSelectedItem(null);
		setHeight.setBackground(new Color(240, 128, 29));
		setHeight.addActionListener(new heightCustomizedListener());
		playerNum=0;
		AIPlayerNum=0;
		wMap=0;
		hMap=0;
		gameResult=new JLabel();
		anotherOne=translucentButton(new Color(255, 255, 0, 70), "再来一局");
		anotherOne.addActionListener(new multiplayerListener());
		quitButton=new JButton("退出游戏");
		quitButton.setBorderPainted(false);
		quitButton.setBackground(new Color(211, 100, 18));
		quitButton.addActionListener(new quitListener());
		loadingGame=new JLabel("加载中...");
		loadingGame.setHorizontalAlignment(JLabel.CENTER);
		creditsColor=new Color(187, 190, 135);
		youStudio=new JLabel("游戏制作：You组",JLabel.CENTER);
		youStudio.setForeground(creditsColor);
		groupLeader=new JLabel("组长：李一然",JLabel.CENTER);
		groupLeader.setForeground(creditsColor);
		groupMembers=new JLabel("组员：王舒遥，王维纲，王宇",JLabel.CENTER);
		groupMembers.setForeground(creditsColor);
		aiLevel=0;
		atIndexPage=false;
		atCreditsPage=false;
		atSettingPage=false;
		atResultPage=false;
		/*try{
			indexBackground=ImageIO.read(GameUI.class.getClassLoader().getResource("indexbackground.png"));
		}catch(IOException e){
			e.printStackTrace();
		}*/
		try{
			indexBackground=ImageIO.read(GameUI.class.getClassLoader().getResource("image/indexBackground.png"));
			creditsBackground=ImageIO.read(GameUI.class.getClassLoader().getResource("image/creditsBackground.png"));
			settingBackground=ImageIO.read(GameUI.class.getClassLoader().getResource("image/settingBackground.png"));
			resultBackground=ImageIO.read(GameUI.class.getClassLoader().getResource("image/resultBackground.png"));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void openGame(){
		msBlack=new Font("微软雅黑", Font.BOLD, 36*GameUI.wOfPanel/2138);
		gameTitle.setFont(new Font("微软雅黑", Font.BOLD, 100*GameUI.wOfPanel/2138));
		multiplayerMode.setFont(new Font("微软雅黑", Font.BOLD, 60*GameUI.wOfPanel/2138));
		multiLabel.setFont(new Font("微软雅黑", Font.BOLD, 100*GameUI.wOfPanel/2138));
		startPlayingTheFxxkingGame.setFont(new Font("微软雅黑", Font.BOLD, 60*GameUI.wOfPanel/2138));
		gameResult.setFont(new Font("微软雅黑", Font.BOLD, 100*GameUI.wOfPanel/2138));
		loadingGame.setFont(new Font("微软雅黑",Font.PLAIN,8*GameUI.wOfPanel/2138));
		youStudio.setFont(new Font("微软雅黑", Font.BOLD, 60*GameUI.wOfPanel/2138));
		groupLeader.setFont(msBlack);
		groupMembers.setFont(msBlack);
		humanNumLabel.setFont(msBlack);
		viewCredits.setFont(msBlack);
		aiNumLabel.setFont(msBlack);
		aiLevelLabel.setFont(msBlack);
		humanPlayersNumBox.setFont(msBlack);
		aiPlayersNumBox.setFont(msBlack);
		setWidth.setFont(msBlack);
		mapSizeLabel.setFont(msBlack);
		setHeight.setFont(msBlack);
		aiLevelBox.setFont(msBlack);
		backToIndex.setFont(msBlack);
		setMapSizeBox.setFont(msBlack);
		customizeMapLabel.setFont(msBlack);
		heightToCustomizeLabel.setFont(msBlack);
		anotherOne.setFont(msBlack);
		quitButton.setFont(msBlack);
		
		gameTitle.setForeground(new Color(211, 100, 18));

		removeAll();
		add(gameTitle);
		gameTitle.setBounds(0, 40*getHeight()/1324, getWidth(), 200*getHeight()/1324);
		add(multiplayerMode);
		multiplayerMode.setBounds(getWidth()/2-250*getWidth()/2138, getHeight()/2-100*getHeight()/1324, 500*getWidth()/2138, 100*getHeight()/1324);
		add(viewCredits);
		viewCredits.setBounds(getWidth()-350*getWidth()/2138, getHeight()-120*getHeight()/1324, 250*getWidth()/2138, 80*getHeight()/1324);
		add(quitButton);
		quitButton.setBounds(100*getWidth()/2138,getHeight()-120*getHeight()/1324,250*getWidth()/2138,80*getHeight()/1324);
		atIndexPage=true;
		atCreditsPage=false;
		atSettingPage=false;
		atResultPage=false;
	}
	
	class multiplayerListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			atIndexPage=false;
			atSettingPage=true;
			atCreditsPage=false;
			atResultPage=false;
			playerNum=0;
			wMap=0;
			hMap=0;
			removeAll();
			add(multiLabel);
			multiLabel.setBounds(getWidth()/2-400*getWidth()/2138, 40*getHeight()/1324, 800*getWidth()/2138, 200*getHeight()/1324);
			add(humanNumLabel);
			humanNumLabel.setBounds(getWidth()/2-280*getWidth()/2138, getHeight()/2-320*getHeight()/1324, 300*getWidth()/2138, 80*getHeight()/1324);
			add(humanPlayersNumBox);
			humanPlayersNumBox.setBounds(getWidth()/2+60*getWidth()/2138, getHeight()/2-310*getHeight()/1324, 200*getWidth()/2138, 60*getHeight()/1324);
			humanPlayersNumBox.setSelectedItem(null);
			add(aiNumLabel);
			aiNumLabel.setBounds(getWidth()/2-280*getWidth()/2138, getHeight()/2-200*getHeight()/1324, 300*getWidth()/2138, 80*getHeight()/1324);
			add(aiPlayersNumBox);
			aiPlayersNumBox.setBounds(getWidth()/2+60*getWidth()/2138, getHeight()/2-190*getHeight()/1324, 200*getWidth()/2138, 60*getHeight()/1324);
			aiPlayersNumBox.setSelectedItem(null);
			add(aiLevelLabel);
			aiLevelLabel.setBounds(getWidth()/2+500*getWidth()/2138, getHeight()/2-200*getHeight()/1324, 150*getWidth()/2138, 80*getHeight()/1324);
			aiLevelLabel.setVisible(false);
			add(aiLevelBox);
			aiLevelBox.setBounds(getWidth()/2+690*getWidth()/2138, getHeight()/2-190*getHeight()/1324, 200*getWidth()/2138, 60*getHeight()/1324);
			aiLevelBox.setSelectedItem(null);
			aiLevelBox.setVisible(false);
			add(mapSizeLabel);
			mapSizeLabel.setBounds(getWidth()/2-280*getWidth()/2138, getHeight()/2-80*getHeight()/1324, 300*getWidth()/2138, 80*getHeight()/1324);
			add(setMapSizeBox);
			setMapSizeBox.setBounds(getWidth()/2+60*getWidth()/2138, getHeight()/2-70*getHeight()/1324, 200*getWidth()/2138, 60*getHeight()/1324);
			setMapSizeBox.setSelectedItem(null);
			add(startPlayingTheFxxkingGame);
			startPlayingTheFxxkingGame.setBounds(getWidth()/2-250*getWidth()/2138,getHeight()/2+400*getHeight()/1324,500*getWidth()/2138,100*getHeight()/1324);
			startPlayingTheFxxkingGame.setEnabled(false);
			add(backToIndex);
			backToIndex.setBounds(getWidth()-350*getWidth()/2138, getHeight()-100*getHeight()/1324, 250*getWidth()/2138, 60*getHeight()/1324);
			revalidate();
			repaint();
		}
	}
	
	class numOfPlayerListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			@SuppressWarnings("unchecked")
			JComboBox<Integer> jComboBox = (JComboBox<Integer>)e.getSource();
			playerNum=(Integer)jComboBox.getSelectedItem();
			if(playerNum!=null&&AIPlayerNum!=null) if(playerNum+AIPlayerNum>1&&hMap>0&&wMap>0&&(AIPlayerNum==0||aiLevel>0)) startPlayingTheFxxkingGame.setEnabled(true);
			else startPlayingTheFxxkingGame.setEnabled(false);
			/*else if(playerNum==6){
				aiPlayersNumBox.removeAllItems();
				aiPlayersNumBox.addItem(0);
				aiPlayersNumBox.setSelectedItem(0);
			}*/
			if(playerNum==null){
				playerNum=0;
				return;
			}
			else if(playerNum==0){
				aiPlayersNumBox.removeItem(0);
				aiPlayersNumBox.removeItem(1);
				for(int i=2;i<7;i++){
					for(int j=0;j<aiPlayersNumBox.getItemCount()+1;j++){
						if(aiPlayersNumBox.getItemAt(j)==null){
							aiPlayersNumBox.insertItemAt(i, j);
							break;
						}
						if(aiPlayersNumBox.getItemAt(j)==i) break;
						if(aiPlayersNumBox.getItemAt(j)<i) continue;
						if(aiPlayersNumBox.getItemAt(j)>i){
							aiPlayersNumBox.insertItemAt(i, j);
							break;
						}
					}
				}
			}
			else if(playerNum==1){
				aiPlayersNumBox.removeItem(0);
				aiPlayersNumBox.removeItem(6);
				for(int i=1;i<6;i++){
					for(int j=0;j<aiPlayersNumBox.getItemCount()+1;j++){
						if(aiPlayersNumBox.getItemAt(j)==null){
							aiPlayersNumBox.insertItemAt(i, j);
							break;
						}
						if(aiPlayersNumBox.getItemAt(j)==i) break;
						if(aiPlayersNumBox.getItemAt(j)<i) continue;
						if(aiPlayersNumBox.getItemAt(j)>i){
							aiPlayersNumBox.insertItemAt(i, j);
							break;
						}
					}
				}
			}
			else{
				for(int i=6;i>6-playerNum;i--){
					aiPlayersNumBox.removeItem(i);
				}
				for(int i=0;i<=6-playerNum;i++){
					for(int j=0;j<aiPlayersNumBox.getItemCount()+1;j++){
						if(aiPlayersNumBox.getItemAt(j)==null){
							aiPlayersNumBox.insertItemAt(i, j);
							break;
						}
						if(aiPlayersNumBox.getItemAt(j)==i) break;
						if(aiPlayersNumBox.getItemAt(j)<i) continue;
						if(aiPlayersNumBox.getItemAt(j)>i){
							aiPlayersNumBox.insertItemAt(i, j);
							break;
						}
					}
				}
			}
		}
	}
	
	class numOfAIListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			AIPlayerNum=(Integer)aiPlayersNumBox.getSelectedItem();
			if(playerNum!=null&&AIPlayerNum!=null) if(playerNum+AIPlayerNum>1&&hMap>0&&wMap>0&&(AIPlayerNum==0||aiLevel>0)) startPlayingTheFxxkingGame.setEnabled(true);
			else startPlayingTheFxxkingGame.setEnabled(false);
			/*else if(AIPlayerNum==6){
				humanPlayersNumBox.removeAllItems();
				humanPlayersNumBox.addItem(0);
				humanPlayersNumBox.setSelectedItem(0);
			}*/
			if(AIPlayerNum==null){
				AIPlayerNum=0;
				return;
			}
			else if(AIPlayerNum==0){
				humanPlayersNumBox.removeItem(0);
				humanPlayersNumBox.removeItem(1);
				for(int i=2;i<7;i++){
					for(int j=0;j<humanPlayersNumBox.getItemCount()+1;j++){
						if(humanPlayersNumBox.getItemAt(j)==null){
							humanPlayersNumBox.insertItemAt(i, j);
							break;
						}
						if(humanPlayersNumBox.getItemAt(j)==i) break;
						if(humanPlayersNumBox.getItemAt(j)<i) continue;
						if(humanPlayersNumBox.getItemAt(j)>i){
							humanPlayersNumBox.insertItemAt(i, j);
							break;
						}
					}
				}
				aiLevelLabel.setVisible(false);
				aiLevelBox.setVisible(false);
				aiLevel=0;
				return;
			}
			else if(AIPlayerNum==1){
				humanPlayersNumBox.removeItem(0);
				humanPlayersNumBox.removeItem(6);
				for(int i=1;i<6;i++){
					for(int j=0;j<humanPlayersNumBox.getItemCount()+1;j++){
						if(humanPlayersNumBox.getItemAt(j)==null){
							humanPlayersNumBox.insertItemAt(i, j);
							break;
						}
						if(humanPlayersNumBox.getItemAt(j)==i) break;
						if(humanPlayersNumBox.getItemAt(j)<i) continue;
						if(humanPlayersNumBox.getItemAt(j)>i){
							humanPlayersNumBox.insertItemAt(i, j);
							break;
						}
					}
				}
			}
			else{
				for(int i=6;i>6-AIPlayerNum;i--){
					humanPlayersNumBox.removeItem(i);
				}
				for(int i=0;i<=6-playerNum;i++){
					for(int j=0;j<humanPlayersNumBox.getItemCount()+1;j++){
						if(humanPlayersNumBox.getItemAt(j)==null){
							humanPlayersNumBox.insertItemAt(i, j);
							break;
						}
						if(humanPlayersNumBox.getItemAt(j)==i) break;
						if(humanPlayersNumBox.getItemAt(j)<i) continue;
						if(humanPlayersNumBox.getItemAt(j)>i){
							humanPlayersNumBox.insertItemAt(i, j);
							break;
						}
					}
				}
			}
			aiLevelLabel.setVisible(true);
			aiLevelBox.setVisible(true);
		}
	}
	
	class levelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(aiLevelBox.getSelectedItem()==null) return;
			if(aiLevelBox.getSelectedItem()!=null) aiLevel=aiLevelBox.getSelectedIndex()+1;
			if(aiLevelBox.getSelectedItem()!=null){
				if(playerNum+AIPlayerNum>0&&hMap>0&&wMap>0&&(AIPlayerNum==0||aiLevel>0)) startPlayingTheFxxkingGame.setEnabled(true);
				else startPlayingTheFxxkingGame.setEnabled(false);
			}
		}
	}
	
	class mapSizeListener implements ActionListener{
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			if(((JComboBox<String>)e.getSource()).getSelectedItem()!=null){
			switch ((String)((JComboBox<String>)e.getSource()).getSelectedItem()) {
				case "大" :
					wMap=28;
					hMap=28;
					break;
				case "中":
					wMap=20;
					hMap=20;
					break;
				case "小":
					wMap=12;
					hMap=12;
					break;
				case "自定义":
					add(customizeMapLabel);
					customizeMapLabel.setBounds(getWidth()/2-280*getWidth()/2138, getHeight()/2+40*getHeight()/1324, 300*getWidth()/2138, 80*getHeight()/1324);
					add(setWidth);
					setWidth.setBounds(getWidth()/2+60*getWidth()/2138, getHeight()/2+50*getHeight()/1324, 200*getWidth()/2138, 60*getHeight()/1324);
					setWidth.setSelectedItem(null);
					add(heightToCustomizeLabel);
					heightToCustomizeLabel.setBounds(getWidth()/2-280*getWidth()/2138, getHeight()/2+160*getHeight()/1324, 300*getWidth()/2138, 80*getHeight()/1324);
					add(setHeight);
					setHeight.setBounds(getWidth()/2+60*getWidth()/2138, getHeight()/2+170*getHeight()/1324, 200*getWidth()/2138, 60*getHeight()/1324);
					setHeight.setSelectedItem(null);
					hMap=0;
					wMap=0;
					startPlayingTheFxxkingGame.setEnabled(false);
					revalidate();
					repaint();
					return;
			}
			remove(customizeMapLabel);
			remove(setWidth);
			remove(heightToCustomizeLabel);
			remove(setHeight);
			if(playerNum+AIPlayerNum>0&&hMap>0&&wMap>0&&(AIPlayerNum==0||aiLevel>0)) startPlayingTheFxxkingGame.setEnabled(true);
			else startPlayingTheFxxkingGame.setEnabled(false);
			repaint();
			}
		}
	}
	
	class widthCustomizedListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			Integer w=(Integer)((JComboBox<Integer>)e.getSource()).getSelectedItem();
			if(w!=null&&hMap!=null){
				wMap=w;
				if(playerNum+AIPlayerNum>0&&hMap>0&&wMap>0&&(AIPlayerNum==0||aiLevel>0)) startPlayingTheFxxkingGame.setEnabled(true);
				else startPlayingTheFxxkingGame.setEnabled(false);
			}
		}
	}
	
	class heightCustomizedListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			Integer h=(Integer)((JComboBox<Integer>)e.getSource()).getSelectedItem();
			if(h!=null&&wMap!=null){
				hMap=h;
				if(playerNum+AIPlayerNum>0&&hMap>0&&wMap>0&&(AIPlayerNum==0||aiLevel>0)) startPlayingTheFxxkingGame.setEnabled(true);
				else startPlayingTheFxxkingGame.setEnabled(false);
			}
		}
	}
	
	
	
	class startPlayingTheFxxkingGameListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			isAI=new int[playerNum+AIPlayerNum];
			for(int i=0;i<playerNum;i++){
				isAI[i]=0;
			}
			for(int i=playerNum;i<playerNum+AIPlayerNum;i++){
				isAI[i]=aiLevel;
			}
			add(loadingGame);
			loadingGame.setBounds(getWidth()/2-150*getWidth()/2138,getHeight()/2+540*getHeight()/1324,300*getWidth()/2138,60*getHeight()/1324);
			repaint();
			frame.remove(thisPage);
			frame.setLayout(null);
			GameUI.mainPanel=new GamePanel(frame,playerNum+AIPlayerNum,wMap,hMap, GameUI.roundParam, GameUI.simpleMapMaker(playerNum+AIPlayerNum,wMap,hMap), GameUI.occupyParam, GameUI.initialMoneyParam, GameUI.fighterParam, GameUI.defendParam,GameUI.incomeParam,isAI);
			
			frame.add(GameUI.mainPanel);
			GameUI.mainPanel.setBounds(0,0,GameUI.wOfPanel,GameUI.hOfPanel);
			GameUI.mainPanel.startGame();
			frame.paintComponents(frame.getGraphics());
		}
	}
	
	class backButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			openGame();
			repaint();
		}
	}
	
	class quitListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
		}
	}
	
	class creditsListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			atIndexPage=false;
			atCreditsPage=true;
			atSettingPage=false;
			atResultPage=false;
			removeAll();
			add(youStudio);
			youStudio.setBounds(getWidth()/2-350*getWidth()/2138, 415*getHeight()/1324, 680*getWidth()/2138, 150*getHeight()/1324);
			add(groupLeader);
			groupLeader.setBounds(getWidth()/2-350*getWidth()/2138, 565*getHeight()/1324, 680*getWidth()/2138, 80*getHeight()/1324);
			add(groupMembers);
			groupMembers.setBounds(getWidth()/2-350*getWidth()/2138, 645*getHeight()/1324, 680*getWidth()/2138, 80*getHeight()/1324);
			add(backToIndex);
			backToIndex.setBounds(GameUI.wOfPanel/2-150*getWidth()/2138, GameUI.hOfPanel-300*getHeight()/1324, 300*getWidth()/2138, 80*getHeight()/1324);
			repaint();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if(atIndexPage) g.drawImage(indexBackground, 0, 0, GameUI.wOfPanel, GameUI.hOfPanel, null);
		else if(atCreditsPage) g.drawImage(creditsBackground, 0, 0, GameUI.wOfPanel, GameUI.hOfPanel, null);
		else if(atSettingPage) g.drawImage(settingBackground, 0, 0, GameUI.wOfPanel, GameUI.hOfPanel, null);
		else if(atResultPage) g.drawImage(resultBackground, 0, 0, GameUI.wOfPanel, GameUI.hOfPanel, null);
		super.setOpaque(false);
		super.paint(g);
	}
	
	public JButton translucentButton(Color color,String text){
		JButton but = new JButton(text) {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                if (getBackground().getAlpha() < 255) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }

        };
        but.setBackground(color);
        but.setMargin(new Insets(0, 0, 0, 0));
        but.setBorderPainted(false);
        but.setOpaque(false);
        //but.setOpaque(opaque);
        return but;
	}
	
	
}



