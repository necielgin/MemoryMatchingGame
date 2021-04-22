import processing.core.PApplet;
import processing.core.PImage;
import java.io.File;

//////////////// FILE HEADER (INCLUDE IN EVERY FILE) //////////////////////////
//
// Title: Self Checkout Kiosk
// Course: CS 300 Fall 2020
//
// Author: Elgini Neci
// Email: neci@wisc.edu
// Lecturer: Hobbes LeGault
//
///////////////////////////////////////////////////////////////////////////////

/**
 * 
 * This class contain all the method for the MemoryGame
 *
 */
public class MemoryGame {

  // Congratulations message
  private final static String CONGRA_MSG = "CONGRATULATIONS! YOU WON!";
  // Cards not matched message
  private final static String NOT_MATCHED = "CARDS NOT MATCHED. Try again!";
  // Cards matched message
  private final static String MATCHED = "CARDS MATCHED! Good Job!";
  // 2D-array which stores cards coordinates on the window display
  private final static float[][] CARDS_COORDINATES =
      new float[][] {{170, 170}, {324, 170}, {478, 170}, {632, 170}, {170, 324}, {324, 324},
          {478, 324}, {632, 324}, {170, 478}, {324, 478}, {478, 478}, {632, 478}};
  // Array that stores the card images filenames
  private final static String[] CARD_IMAGES_NAMES = new String[] {"ball.png", "redFlower.png",
      "yellowFlower.png", "apple.png", "peach.png", "shark.png"};
  private static PApplet processing; // PApplet object that represents
  // the graphic display window
  private static Card[] cards; // one dimensional array of cards
  private static PImage[] images; // array of images of the different cards
  private static Card selectedCard1; // First selected card
  private static Card selectedCard2; // Second selected card
  private static boolean winner; // boolean evaluated true if the game is won,
  // and false otherwise
  private static int matchedCardsCount; // number of cards matched so far
  // in one session of the game
  private static String message; // Displayed message to the display window

  /**
   * This is the main method that calls the method of the game
   * 
   * @param args
   */
  public static void main(String[] args) {
    Utility.startApplication();
  }

  /**
   * Defines the initial environment properties of this game as the program starts
   */
  public static void setup(PApplet processing) {
    MemoryGame.processing = processing;

    images = new PImage[CARD_IMAGES_NAMES.length];
    // load image file as PImage object and store its reference into images[]
    for (int i = 0; i < images.length; i++) {
      images[i] = processing.loadImage("images" + File.separator + CARD_IMAGES_NAMES[i]);
    }
    startNewGame();
  }

  /**
   * Initializes the Game
   */
  public static void startNewGame() {
    selectedCard1 = null;
    selectedCard2 = null;
    matchedCardsCount = 0;
    winner = false;
    message = "";
    cards = new Card[CARDS_COORDINATES.length];
    int[] mixedUp = Utility.shuffleCards(cards.length);
    for (int i = 0; i < cards.length; i++) {
      cards[i] = new Card(images[mixedUp[i]], CARDS_COORDINATES[i][0], CARDS_COORDINATES[i][1]);
    }
  }

  /**
   * Callback method called each time the user presses a key
   */
  public static void keyPressed() {
    if (processing.key == ('n') || processing.key == ('N')) {
      startNewGame();
    }
  }

  /**
   * Displays a given message to the display window
   * 
   * @param message to be displayed to the display window
   */
  public static void displayMessage(String message) {
    processing.fill(0);
    processing.textSize(20);
    processing.text(message, processing.width / 2, 50);
    processing.textSize(12);
  }

  /**
   * Callback method draws continuously this application window display
   */
  public static void draw() {
    // Set the color used for the background of the Processing window
    processing.background(245, 255, 250); // Mint cream color
    for (int i = 0; i < cards.length; i++) {
      // cards[i].setVisible(true);
      cards[i].draw();
    }
    displayMessage(message);
  }

  /**
   * Checks whether the mouse is over a given Card
   * 
   * @return true if the mouse is over the storage list, false otherwise
   */
  public static boolean isMouseOver(Card card) {
    if (processing.mouseX <= card.getX() + card.getImage().width / 2
        && processing.mouseX >= card.getX() - card.getImage().width / 2
        && processing.mouseY <= card.getY() + card.getImage().height / 2
        && processing.mouseY >= card.getY() - card.getImage().height / 2) {
      return true;
    }

    return false;
  }

  /**
   * Callback method called each time the user presses the mouse
   */
  public static void mousePressed() {
    int temp1 = 0; // keeps track where is matched card position
    int temp2 = 0; // keeps track where is matched card position
    int visibleCards = 0;// keeps track how many visible cards there are

    // deselects cards that are already matched and "hides" them
    if ((selectedCard1 != null && selectedCard2 != null)) {
      if (matchingCards(selectedCard1, selectedCard2)) {
        selectedCard1.deselect();
        selectedCard2.deselect();
        selectedCard1 = null;
        selectedCard2 = null;
        message = "";
      } else {
        message = "";
        selectedCard1.setVisible(false);
        selectedCard2.setVisible(false);
        selectedCard1.deselect();
        selectedCard2.deselect();
        selectedCard1 = null;
        selectedCard2 = null;
      }
    }
    // selects the cards if mouse over the card
    // if the card is matched it doesn't select it
    for (int i = 0; i < cards.length; i++) {
      temp1++;
      temp2++;
      if (isMouseOver(cards[i])) {
        if (selectedCard1 == null && (cards[i].isMatched() == false)) {
          selectedCard1 = cards[i];
          selectedCard1.setVisible(true);
          selectedCard1.select();
          break;
        } else {
          if (cards[i].isMatched() == false) {
            selectedCard2 = cards[i];
            selectedCard2.setVisible(true);
            selectedCard2.select();
            break;
          }
        }
      }
    }
    // Checks if the cards match shows the according message
    if ((selectedCard1 != null && selectedCard2 != null)) {
      if (matchingCards(selectedCard1, selectedCard2)) {
        message = MATCHED;
        cards[temp1 - 1].setMatched(true);
        cards[temp2 - 1].setMatched(true);
      } else {
        message = NOT_MATCHED;
      }
    }
    // check if all the images are visible, if so means the player won
    for (int j = 0; j < cards.length; j++) {
      if (cards[j].isVisible()) {
        visibleCards++;
      }
      if (visibleCards == 12) {
        message = CONGRA_MSG;
        winner = true;
      }
    }
  }

  /**
   * Checks whether two cards match or not
   * 
   * @param card1 reference to the first card
   * @param card2 reference to the second card
   * @return true if card1 and card2 image references are the same, false otherwise
   */
  public static boolean matchingCards(Card card1, Card card2) {
    if (card1.getImage().equals(card2.getImage())) {
      return true;
    }

    return false;
  }
}


