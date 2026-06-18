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

By default the app runs over plain HTTP on port `8080` (Railway-friendly).

```bash
mvn spring-boot:run
```

### Run locally with HTTPS (for pinning tests)

Use the `local` profile to enable embedded HTTPS on `8443`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Deploy on Railway

- Railway terminates TLS at the edge and forwards HTTP to your app.
- The default profile already runs plain HTTP, so no extra config is required.
- Do NOT set `SERVER_SSL_ENABLED=true` on Railway.

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

### 1b) Get server pins (certificate + public key)

```bash
curl -k https://localhost:8443/api/pinning/server-pins
```

Example response:

```json
{
  "certificatePin": "sha256/<certificate-pin>",
  "certificateSha256Hex": "<hex-value>",
  "publicKeyPin": "sha256/<public-key-pin>",
  "publicKeySha256Hex": "<hex-value>",
  "algorithm": "SHA-256",
  "format": "sha256/<base64>",
  "note": "Use publicKeyPin for mobile SSL pinning in most clients"
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
