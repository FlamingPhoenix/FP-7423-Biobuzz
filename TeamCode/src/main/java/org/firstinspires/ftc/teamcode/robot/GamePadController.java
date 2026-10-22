package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamePadController {
    public interface Action{
        void execute(boolean[] pressed);
    }
    public enum Button{
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        LEFT_BUMPER,
        RIGHT_BUMPER,
        LEFT_TRIGGER,
        RIGHT_TRIGGER,
        A_BUTTON,
        B_BUTTON,
        X_BUTTON,
        Y_BUTTON,
        START_BUTTON,
        BACK_BUTTON
    }
    boolean[] dpadPressed = new boolean[4];
    boolean[] bumperPressed = new boolean[2];
    Gamepad gamepad;
    public GamePadController(Gamepad gamepad) {
        this.gamepad = gamepad;
    }
    Action dpadAction;
    Action bumperAction;
    Action triggerAction;
    Action buttonAction;
    Action optionAction;


    public void update(){
        updateDpad();

    }
    private void updateDpad(){
        boolean[] dpad = new boolean[4];
        if(gamepad.dpad_up && !dpadPressed[0]){
            dpad[0] = true;
            dpadPressed[0] = true;
        } else if(!gamepad.dpad_up){
            dpadPressed[0] = false;
        }
        if(gamepad.dpad_down && !dpadPressed[1]){
            dpad[1] = true;
            dpadPressed[1] = true;
        } else if(!gamepad.dpad_down){
            dpadPressed[1] = false;
        }
        if(gamepad.dpad_left && !dpadPressed[2]){
            dpad[2] = true;
            dpadPressed[2] = true;
        } else if(!gamepad.dpad_left){
            dpadPressed[2] = false;
        }
        if(gamepad.dpad_right && !dpadPressed[3]){
            dpad[3] = true;
            dpadPressed[3] = true;
        } else if(!gamepad.dpad_right){
            dpadPressed[3] = false;
        }
    }
}
