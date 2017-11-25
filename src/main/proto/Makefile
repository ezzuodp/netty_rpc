
PROTOBUF_DIR=/usr
PROTO_OPTIONS=--proto_path=. --proto_path=/usr/include

all: echo_service

echo_service : echo_service.pb.cc

%.pb.cc: %.proto
	$(PROTOBUF_DIR)/bin/protoc $(PROTO_OPTIONS) --java_out=../java --cpp_out=../cpp $<