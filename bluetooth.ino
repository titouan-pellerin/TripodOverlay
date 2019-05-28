#include <Grove_I2C_Motor_Driver.h>
#include <SoftwareSerial.h>
#include <Stepper.h>

#define I2C_ADDRESS 0x0f

int nombrePas = 3072;
int rxPin = 10;
int txPin = 11;
int Data;

SoftwareSerial mySerial(rxPin, txPin);
//6 = N2, 3 = N4, 5 = N3, 9 = N1 
Stepper myStepper(nombrePas,6,3,5,9);


void setup() {
  pinMode(rxPin, INPUT);
  pinMode(txPin, OUTPUT);  
  mySerial.begin(9600);
  Serial.begin(9600);
  Motor.begin(I2C_ADDRESS);
  myStepper.setSpeed(2);
  
}

void loop() {
 /*  Motor.StepperRun(100, 1);
   delay(1000);
   Motor.StepperRun(-100, 1);*/
   if (mySerial.available()){
    Data=mySerial.read();
    Serial.println(Data);
    if(Data==1){
      myStepper.step(5);
    }
    else if(Data==2){
      //Motor.StepperRun(-100, 1);
      //delay(1000);
      myStepper.step(-5);
    }
    else if(Data==0){
      //Motor.stop(MOTOR1);
      //Motor.stop(MOTOR2);
      //Motor.StepperRun(0, 1);
      myStepper.step(0);
    }
  }
}
