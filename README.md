# server-ssl-pinning

Simple Spring Boot HTTPS service to understand the server certificate pinning concept.

## What this project demonstrates

- Runs a local HTTPS server using a self-signed certificate.
- Exposes API to fetch the server certificate pin (`sha256/<base64>` format).
- Exposes API to validate a provided pin against the server certificate pin.

## Service details

- Package / service name: `com.ashi.sslserverPinning`
- HTTPS port: `8443`
- Certificate files:
  - `src/main/resources/certs/server-cert.pem`
  - `src/main/resources/certs/server-key.pem`
  - `src/main/resources/certs/server-keystore.p12`

## Recreate certificates (optional)

```bash
mkdir -p src/main/resources/certs
openssl req -x509 -newkey rsa:2048 -sha256 -days 3650 -nodes \
  -keyout src/main/resources/certs/server-key.pem \
  -out src/main/resources/certs/server-cert.pem \
  -subj "/C=IN/ST=KA/L=Bangalore/O=Ashi/OU=Dev/CN=localhost"
openssl pkcs12 -export \
  -in src/main/resources/certs/server-cert.pem \
  -inkey src/main/resources/certs/server-key.pem \
  -out src/main/resources/certs/server-keystore.p12 \
  -name sslserver -passout pass:changeit
```

## Run

```bash
mvn spring-boot:run
```

## Deploy on Railway

- Railway terminates TLS at the edge and forwards HTTP to your app.
- Use profile `railway` so the app runs without embedded HTTPS.

Set this Railway variable:

```bash
SPRING_PROFILES_ACTIVE=railway
```

Important for pinning:

- For clients calling the Railway URL, pin the certificate/key served by Railway.
- Do not pin `server-keystore.p12` values for Railway URLs, because that cert is local app TLS only.

## API

### 1) Get server pin

```bash
curl -k https://localhost:8443/api/pinning/server-pin
```

Example response:

```json
{
  "pin": "sha256/<server-pin-value>",
  "sha256Hex": "<hex-value>",
  "note": "Use pin value in /api/pinning/validate to simulate client pin check"
}
```

### 2) Validate provided pin

```bash
curl -k -X POST https://localhost:8443/api/pinning/validate \
  -H "Content-Type: application/json" \
  -d '{"pin":"sha256/<server-pin-value>"}'
```

Example response:

```json
{
  "matched": true,
  "providedPin": "sha256/<server-pin-value>",
  "expectedPin": "sha256/<server-pin-value>",
  "message": "Pin matches server certificate"
}
```

## Test

```bash
mvn test
```
