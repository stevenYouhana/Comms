void setup() {
  Serial.begin(9600);
  pinMode(13, OUTPUT);
  Serial.write("Hello, Java");
}
void writer() {
  Serial.write("Hello, Java");
}
void loop() {
  Serial.write("hi java");
  delay(1000);
 if (Serial.available() > 0) {
//  char inByte = Serial.read();
  int inByte = Serial.read();
  Serial.println(inByte);
  switch (inByte) {
    case '1':
        Serial.write("turning ON"); 
        digitalWrite(13, HIGH);
      break;
    case '2': 
        Serial.write("turning OFF"); 
        digitalWrite(13, LOW);
      break;
    case '/n': break;
    default: Serial.write("TX");
      break;
  }
//  delay(10);
//  writer();
 }
 delay(10);  
// Serial.flush();
}
