package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import server.ChatServer;
import ui.ConnectView;
import ui.MainWindow;
import org.junit.Test;
import static org.junit.Assert.*;

public class ModelTest {
	
	//logged in
	@Test
	public void successfulLogOut()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4445);
		Thread t = new Thread(new Runnable(){
			public void run(){	
				server.start();
			}
		});
		t.start();
		
		Model model = new Model("localhost",4445);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		model.outputTranscript(bos);
		model.start(new MainWindow());

		Thread.sleep(10);//wait for the connection successful message
		model.login("evan", "123");
		
		Thread.sleep(10);//wait for welcome message to come back
		
		model.logout();
		String expected =   "login evan a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3\n"+
							"online\n"+
							"logout\n";
		
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	@Test
	public void unsuccessfulLogOut()throws IOException{
		final ChatServer server = new ChatServer(4446);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		Model model = new Model("localhost",4446);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		model.outputTranscript(bos);
		model.start(new MainWindow());

		
		model.login("evan", "123");
		//try to log out immdediatley after login, no time for welcom message to get back, so logout never sent
		model.logout();
		String expected ="login evan a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3\n";
		//logout is never sent becuase you weren't online
		
		assertEquals(bos.toString(),expected);
		server.kill();
	}
	
	@Test
	public void invite()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4447);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		Thread.sleep(10);//wait for the connection successful message
		Model model = new Model("localhost",4447);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		model.outputTranscript(bos);
		model.start(new MainWindow());

		
		//model.login("evan", "123");
		model.inviteContact("jeff", 65);
		String expected = "invite jeff 65\n";
		
		assertEquals(bos.toString(),expected);
		server.kill();
	}
	
	
	
	
	@Test
	public void typingStatus(){
		
	}
	
	@Test
	public void createRoomTest()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4449);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4449");
		Thread.sleep(10);
		
		Model model = cv.getModel();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);

		model.login("evan", "123");
		Thread.sleep(10);
		model.createRoom();
		Thread.sleep(1000);
		String expected = "login evan a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3\n"+
				"online\n"+
				"create\n"+
				"roomUsers 0\n";
		
		assertEquals(bos.toString(),expected);
		server.kill();
	}
	
	@Test
	public void createAndInviteTest()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4450);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4450");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		
		
		model.login("evan", "123");
		Thread.sleep(10);//wait for welcome message
		model.createAndInvite("jeff");
		Thread.sleep(1000);//wait for the server to return the room created message
		
		String expected = "login evan a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3\n"+
						"online\n"+
						"create\n"+
						"roomUsers 0\n"+
						"invite jeff 0\n";	
		System.out.println("\n\nBOS START");
		System.out.println(bos);
		System.out.println(bos2);
		System.out.println("BOS END\n\n");
		assertEquals(bos.toString(),expected);
		server.kill();
	}
	
	//logged out
	@Test
	public void successfulRegister()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4451);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4451");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		
		
		model.register("bob", "123","123");
		
		Thread.sleep(1000);//wait for the server to return the message
		
		String expected = "register bob a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3\n";	
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	
	@Test
	public void notAlphaNumericRegister()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4452);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4452");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		
		
		model.register("bob", "123(","123(");
		System.out.println("charles was retarded");
		
		Thread.sleep(1000);//wait for the server to return the message
		
		String expected = "";	//client doesn't send a register message
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	
	@Test
	public void notPassConf()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4453);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4453");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		
		
		model.register("bob", "123","1234");
		
		Thread.sleep(1000);//wait for the server to return the message
		
		String expected = "";	//client doesn't send a register message
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	
	@Test
	public void testSay()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4454);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4454");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		
		model.say("hello", 4);
		
		//Thread.sleep(1000);//wait for the server to return the message
		
		String expected = "say 4 hello\n";	//client doesn't send a register message
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	
	@Test
	public void testTypingMessages()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4455);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4455");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		try{
			model.notifyChangedTypingStatus(0, 0);
			model.notifyChangedTypingStatus(0, 1);
			model.notifyChangedTypingStatus(0, 2);
		}catch(InvalidStatusException e){
			
		}
		
		//Thread.sleep(1000);//wait for the server to return the message
		
		String expected = "idle 0\n"+
						"typing 0\n"+
						"enteredText 0\n";	//client doesn't send a register message
		
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	
	@Test
	public void testDecline()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4456);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4456");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		
		
		model.declineInvite(0);
		
		
		//Thread.sleep(1000);//wait for the server to return the message
		
		String expected = "decline 0\n";	//client doesn't send a register message
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
	
	@Test
	public void testAccept()throws IOException,InterruptedException{
		final ChatServer server = new ChatServer(4457);
		Thread t = new Thread(new Runnable(){
			public void run(){
				server.start();
			}
		});
		t.start();
		MainWindow mainwindow = new MainWindow();
		ConnectView cv =mainwindow.getConnectView();
		cv.debugConnect("localhost", "4457");
		Thread.sleep(10);
		Model model = cv.getModel();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		model.outputTranscript(bos);
		model.acceptInvite(0);
		
		String expected = "accept 0\n"+
							"roomUsers 0\n";	//client doesn't send a register message
		
		assertEquals(bos.toString(),expected);
		server.kill();
		
	}
}
