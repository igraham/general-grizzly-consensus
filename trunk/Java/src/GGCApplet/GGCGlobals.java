package GGCApplet;

/**
*
* @author Stephen Kent
*
*         This is where all the globals we use should go.
*
*/
public enum GGCGlobals
{
   INSTANCE;
   int COMMUNICATION_PORT = 10515;
   int MESSAGE_EVENT_ID = 8722736; // Just a large random number so that our id
                                   // should not interfere with other action
                                   // ids.
}