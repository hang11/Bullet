import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameFrame extends JFrame{
	private GamePanel p = new GamePanel();
	public GameFrame(){
		setTitle("사격게임");
		setContentPane(p);
		setSize(500,500);
		setVisible(true);
		p.startGame();
	}
	
	
	
	class GamePanel extends JPanel{
		JLabel baseLabel;
		JLabel targetLabel;
		JLabel bulletLabel;
		public GamePanel(){
			setBackground(Color.YELLOW);
			setLayout(null);
			ImageIcon Icon = new ImageIcon("images/target.jpg");
			targetLabel = new JLabel(Icon);
			targetLabel.setSize(Icon.getIconWidth(),Icon.getIconHeight());
			targetLabel.setLocation(0,0);
			add(targetLabel);
			
			baseLabel = new JLabel();
			baseLabel.setSize(40,40);
			baseLabel.setOpaque(true);
			baseLabel.setBackground(Color.BLACK);
			
			baseLabel.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					JLabel la = (JLabel)e.getSource();
					la.requestFocus();
				}
			});
			
			
			add(baseLabel);
			
			bulletLabel = new JLabel();
			bulletLabel.setSize(10,10);
			bulletLabel.setOpaque(true);
			bulletLabel.setBackground(Color.RED);
			add(bulletLabel);
		}
		
		public void startGame(){
			baseLabel.requestFocus();
			baseLabel.setLocation(this.getWidth()/2-20,this.getHeight()-40);
			bulletLabel.setLocation(this.getWidth()/2-5,this.getHeight()-40-10);
			TargetThread targetThread = new TargetThread(targetLabel);
			targetThread.start();
			
			baseLabel.addKeyListener(new KeyAdapter(){
				BulletThread bulletThread=null;
				public void keyPressed(KeyEvent e){
					if(e.getKeyChar() == '\n'){
						if(bulletThread==null || !bulletThread.isAlive()){
						 bulletThread = new BulletThread(bulletLabel,targetLabel,targetThread);
						bulletThread.start();
						}
					}
				}
			});
		}
		
		class TargetThread extends Thread{
			JLabel TargetLabel;
			public TargetThread(JLabel tl){
				this.TargetLabel = tl;
			}
			public void run(){
				while(true){
					ImageIcon deathIcon = new ImageIcon("images/target2.png");
					int x = TargetLabel.getX()+5;
					int y = TargetLabel.getY();
					if(x>GamePanel.this.getWidth() - targetLabel.getWidth())
						x=0;
					TargetLabel.setLocation(x,y);
					TargetLabel.getParent().repaint();
					try {						
						sleep(200);
					} catch (InterruptedException e) {
						Icon aliveIcon = targetLabel.getIcon();
						targetLabel.setIcon(deathIcon);
						try {
							sleep(2000);
							targetLabel.setLocation(0,0);
							targetLabel.setIcon(aliveIcon);
						} catch (InterruptedException e1) {
							
						}
						
					}
				}
			}
		}
		class BulletThread extends Thread{
			JLabel bulletLabel;
			JLabel target;
			TargetThread targetThread;
			public BulletThread(JLabel bl, JLabel targetLabel,TargetThread targetThread){
				this.bulletLabel = bl;
				this.target = targetLabel;
				this.targetThread = targetThread;
			}
			public boolean hit(){
				if(targetContaines(bulletLabel.getX(),bulletLabel.getY()) || 
						targetContaines(bulletLabel.getX(),bulletLabel.getY()+bulletLabel.getHeight())||
						targetContaines(bulletLabel.getX()+bulletLabel.getWidth(),bulletLabel.getY())||
						targetContaines(bulletLabel.getX()+bulletLabel.getWidth(),bulletLabel.getY()+bulletLabel.getHeight()))
				return true;
				else
					return false;
			}
			
			
			
			public boolean targetContaines(int x, int y){
				if(target.getX() <= x && x <= target.getX()+target.getWidth()
				&& target.getY() <= y && y <= target.getY()+target.getHeight())
				return true;
				else
					return false;
			}
			
			public void run(){
				//long speed =90;
				while(true){
					if(hit()){
//						target.setLocation(0,0);
//						target.getParent().repaint(); 
						targetThread.interrupt();
						int x = bulletLabel.getX();
						int y = GamePanel.this.getHeight()-10-40;
						bulletLabel.setLocation(x, y);
						return;
					}
					int x = bulletLabel.getX();
					int y = bulletLabel.getY()-5;
					
					if(y<0){
						y=GamePanel.this.getHeight()-40-10;
						bulletLabel.setLocation(x,y);
						return;
					}
					bulletLabel.setLocation(x,y);
					bulletLabel.getParent().repaint();
					
					try {
						sleep(15);
						//speed-=1;
					} catch (InterruptedException e) {
						
					}
				}
			}
		}
	}
	
	public static void main(String[] main){
		new GameFrame();
	}
}
