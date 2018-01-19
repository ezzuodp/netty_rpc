
PROTOBUF_DIR=/usr
PROTO_OPTIONS=--proto_path=. --proto_path=/usr/include

all: echo_service

echo_service : echo_service.pb.java echo_service.pb.cc

%.pb.java: %.proto
	$(PROTOBUF_DIR)/bin/protoc $(PROTO_OPTIONS) --java_out=../java $<

%.pb.cc: %.proto
	mkdir -p ../cpp
	$(PROTOBUF_DIR)/bin/protoc $(PROTO_OPTIONS) --cpp_out=../cpp $<