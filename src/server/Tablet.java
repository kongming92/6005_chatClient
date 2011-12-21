package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tablet {
    
	private List<Line> lines;
	
	public Tablet(){
		lines = Collections.synchronizedList(new ArrayList<Line>());
	}
	
	/**
	 * Add a line to the Tablet
	 * @param username - requires username be a non-null String, else do nothing
	 * @param text - requires text be a non-null String, else do nothing
	 */
	public synchronized void addLine(String username, String text) {
	    if (username != null && text != null) {
	        lines.add(new Line(username, text));
	    }
	}
	
	/**
	 * Immutable datatype representing a line in a chat window
	 * Contains String:username of user that said the line, and String:lineText of the actual text
	 */
	private class Line{
		private final String username;
		private final String lineText;
		
		/**
		 * Creates a new Line object
		 * @param username - requires username be a non-null String
		 * @param said - requires said be a non-null String
		 * @throws IllegalArgumentException if preconditions on the arguments not satisfied
		 */
		public Line(String username,String said){
		    if (username == null || said == null) {
		        throw new IllegalArgumentException("ERROR: null arguments given to Line constructor");
		    }
			this.username = username;
			this.lineText = said;
		}
		public String getUsername() {
		    return username;
		}
		public String getLineText() {
		    return lineText;
		}
	}
	
	
}
