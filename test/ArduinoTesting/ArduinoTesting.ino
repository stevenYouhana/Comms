void setup() {
  Serial.begin(9600);
  pinMode(13, OUTPUT);
}
void writer() {
  Serial.write("Hello, Java");
}
void loop() {
  Serial.write('R');
 if (Serial.available() > 0) {
  Serial.write("RX OK");
  char inByte = Serial.read();
  switch (inByte) {
    case 'a': Serial.write("HIGH"); digitalWrite(13, HIGH);
      break;
    case 'b': Serial.write("LOW"); digitalWrite(13, LOW);
      break;
    default: ;
  }
  delay(1000);
//  writer();
 }
 delay(1000);  
 Serial.flush();
}
