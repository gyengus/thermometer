#include <OneWire.h>
#include <timer.h>

#define LED 13
#define ONEWIREPIN 16 /* A2 */
#define WAIT_FOR_TEMPERATURE 750
#define LED_SHORT_DELAY 100
#define LED_LONG_DELAY 2000
#define ERROR_PREFIX String("ERROR: ")
#define READTEMP_CMD "READTEMP\n"

enum ledStates {longOff, firstShortOn, shortOff, secondShortOn};

OneWire  ds(ONEWIREPIN);
auto timer = timer_create_default();

byte data[9];
byte type_s;
float temperature;
bool inProgress = false;
int ledState = longOff;

void setup(void) {
  pinMode(LED, OUTPUT);
  Serial.begin(57600);
  detectSensor();
  timer.every(LED_LONG_DELAY, toggle_led);
}

void loop(void) {
  if (!inProgress) {
    continuousMeasurement();
  }
  if (gotReadTempCommand()) {
    Serial.println(temperature);
  }

  timer.tick();
}

bool continuousMeasurement() {
  inProgress = true;
  startConvertT();
  timer.in(WAIT_FOR_TEMPERATURE, [](void*) -> bool {
    readTemp();
    if (isCRCValid(data, 8)) {
      float temp = convertTemp();
      if (temp == 85) {
        Serial.println(ERROR_PREFIX + "Something went wrong, got 85");
      } else {
        temperature = temp;
      }
    } else {
      Serial.println(ERROR_PREFIX + "Temperature's CRC is invalid.");
    }
    inProgress = false;
    return false;
  });

  return false;
}

bool gotReadTempCommand(void) {
  while (Serial.available() > 0) {
    String received = Serial.readString();
    if (received.equals(READTEMP_CMD)) {
      return true;
    } else {
      Serial.println(ERROR_PREFIX + "Unknown command: " + received + ".");
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
    Serial.println(ERROR_PREFIX + "No sensor detected.");
    halt();
  }

  if (!isCRCValid(addr, 7)) {
    Serial.println(ERROR_PREFIX + "Sensor address's CRC is invalid");
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
      Serial.println(ERROR_PREFIX + "Device is not a DS18x20 family device.");
      halt();
  }
}

bool isCRCValid(byte data[9], byte index) {
  if (OneWire::crc8(data, index) != data[index]) {
      return false;
  }
  return true;
}

bool toggle_led(void) {
  ledState++;
  if (ledState > secondShortOn) ledState = longOff;
  switch (ledState) {
    case longOff:
      digitalWrite(LED, 0);
      timer.every(LED_LONG_DELAY, toggle_led);
      break;
    case firstShortOn:
      digitalWrite(LED, 1);
      timer.every(LED_SHORT_DELAY, toggle_led);
      break;
    case shortOff:
      digitalWrite(LED, 0);
      timer.every(LED_SHORT_DELAY, toggle_led);
      break;
    case secondShortOn:
      digitalWrite(LED, 1);
      timer.every(LED_SHORT_DELAY, toggle_led);
      break;
  }
  return false;
}

void halt(void) {
  Serial.println("HALTED");
  while(1);
}
