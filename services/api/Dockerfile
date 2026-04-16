FROM golang:1.24.2-alpine AS builder
WORKDIR /src
COPY go.mod ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o /out/openreel-api ./cmd/server

FROM alpine:3.22
WORKDIR /app
COPY --from=builder /out/openreel-api /usr/local/bin/openreel-api
EXPOSE 8080
CMD ["/usr/local/bin/openreel-api"]
