#include <OneWire.h>
#include <timer.h>

#define LED 13
#define ONEWIREPIN 16

OneWire  ds(ONEWIREPIN);  // A2
auto timer = timer_create_default();

byte data[9];
byte type_s;

void setup(void) {
  pinMode(LED, OUTPUT);
  Serial.begin(57600);
  detectSensor();
  timer.every(2000, toggle_led);
}

void loop(void) {
  if (gotReadTempCommand()) {
    startConvertT();  
    delay(750);
    readTemp();
    if (isCRCValid(data, 8)) {
      Serial.println(convertTemp());
    } else {
      Serial.println("ERROR: CRC is invalid");
    }
  }

  timer.tick();
}

bool gotReadTempCommand(void) {
  while (Serial.available() > 0) {
    String received = Serial.readString();
    if (received.equals("READTEMP")) {
      return true;
    } else {
      Serial.println("ERROR: Unknown command: " + received + ".");
      return false;
    }
  }
  return false;
}

void readTemp(void) {
  byte i;
  ds.reset();
  ds.skip();
  ds.write(0xBE);
  for ( i = 0; i < 9; i++) {
    data[i] = ds.read();
  }
}

void startConvertT(void) {
  ds.reset();
  ds.skip();
  ds.write(0x44, 1);
}

float convertTemp(void) {
  int16_t raw = (data[1] << 8) | data[0];
  if (type_s) {
    raw = raw << 3;
    if (data[7] == 0x10) {
      raw = (raw & 0xFFF0) + 12 - data[6];
    }
  } else {
    byte cfg = (data[4] & 0x60);
    if (cfg == 0x00) raw = raw & ~7;
    else if (cfg == 0x20) raw = raw & ~3;
    else if (cfg == 0x40) raw = raw & ~1;
  }
  return (float)raw / 16.0;
}

void detectSensor(void) {
  byte addr[8];
  if (!ds.search(addr)) {
    Serial.println("ERROR: No sensor detected.");
    halt();
  }

  if (!isCRCValid(addr, 7)) {
    Serial.println("ERROR: Sensor address's CRC is invalid");
    halt();
  }

  switch (addr[0]) {
    case 0x10:
      type_s = 1;
      break;
    case 0x28:
    case 0x22:
      type_s = 0;
      break;
    default:
      Serial.println("ERROR: Device is not a DS18x20 family device.");
      halt();
  }
}

bool isCRCValid(byte data[9], byte index) {
  if (OneWire::crc8(data, index) != data[index]) {
      Serial.println("ERROR: CRC is not valid!");
      return false;
  }
  return true;
}

bool toggle_led(void *) {
  byte ledState = digitalRead(LED);
  if (ledState) {
    timer.every(2000, toggle_led);
  } else {
    timer.every(250, toggle_led);
  }
  digitalWrite(LED, !ledState);
  return false;
}

void halt(void) {
  Serial.println("HALTED");
  while(1);
}
