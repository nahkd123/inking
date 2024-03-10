# Java
COPY_OUTPUT := src/main/resources/natives

# C#
SLN := Inking.Otd
CSPROJ := $(SLN)/Inking.Otd.csproj
DOTNET_VERSION := net8.0
DOTNET_CONFIGURATION := Release
LIBNAME := Inking.Otd

rid_ext_win-x64 := dll
rid_ext_win-arm64 := dll
rid_ext_linux-x64 := so
rid_ext_linux-arm64 := so
rid_ext_osx-x64 := dylib
rid_ext_osx-arm64 := dylib
SOURCE := $(wildcard $(SLN)/*.cs)
ARCHITECTURE := x64 arm64
PLATFORM := win linux osx
RID := $(foreach a,$(PLATFORM),$(foreach b,$(ARCHITECTURE),$(a)-$(b)))

# Detect current RID
# Cross compile can be done by manually execute "make <...list of RIDs>"
# Would be nice if there is a way to detect installed toolchains
ifeq ($(OS),Windows_NT)
	ifeq ($(PROCESSOR_ARCHITEW6432),AMD64)
		CRID := win-x64
	else
		ifeq ($(PROCESSOR_ARCHITECTURE),AMD64)
			CRID := win-x64
		endif
		ifeq ($(PROCESSOR_ARCHITECTURE),ARM64)
			CRID := win-arm64
		endif
	endif
else
	UNAME := $(shell uname -s)
	ifeq ($(UNAME),Linux)
		CRID := linux
	endif
	ifeq ($(UNAME),Darwin)
		CRID := osx
	endif

	UNAME_P := $(shell uname -p)
	ifeq ($(UNAME_P),x86_64)
		CRID := $(CRID)-x64
	endif
	ifeq ($(UNAME_P),aarch64)
		CRID := $(CRID)-arm64
	endif
	ifeq ($(UNAME_P),arm64)
		CRID := $(CRID)-arm64
	endif
endif

# Main target: Build for the host's platform
current-platform: $(CRID)

# All possible targets
# x86 is not supported because NativeAOT (you shouldn't use 32-bit system anyways)
win-x64: aot-win-x64 $(COPY_OUTPUT)/win-x64/$(LIBNAME).dll $(COPY_OUTPUT)/win-x64/$(LIBNAME).dll.sha1
win-arm64: aot-win-arm64 $(COPY_OUTPUT)/win-arm64/$(LIBNAME).dll $(COPY_OUTPUT)/win-arm64/$(LIBNAME).dll.sha1
linux-x64: aot-linux-x64 $(COPY_OUTPUT)/linux-x64/$(LIBNAME).so $(COPY_OUTPUT)/linux-x64/$(LIBNAME).so.sha1
linux-arm64: aot-linux-arm64 $(COPY_OUTPUT)/linux-arm64/$(LIBNAME).so $(COPY_OUTPUT)/linux-arm64/$(LIBNAME).so.sha1
osx-x64: aot-osx-x64 $(COPY_OUTPUT)/osx-x64/$(LIBNAME).dylib $(COPY_OUTPUT)/osx-x64/$(LIBNAME).dylib.sha1
osx-arm64: aot-osx-arm64 $(COPY_OUTPUT)/osx-arm64/$(LIBNAME).dylib $(COPY_OUTPUT)/osx-arm64/$(LIBNAME).dylib.sha1

# Copy
COPY_TARGET := $(foreach rid,$(RID),$(COPY_OUTPUT)/$(rid)/$(LIBNAME).$(call rid_ext_$(rid)))

extract_filename_from_copy_target = \
	$(patsubst x64/%,%,\
	$(patsubst arm64/%,%,\
	$(patsubst $(COPY_OUTPUT)/win-%,%,\
	$(patsubst $(COPY_OUTPUT)/linux-%,%,\
	$(patsubst $(COPY_OUTPUT)/osx-%,%,\
	$(1))))))
extract_rid_from_copy_target = \
	$(patsubst $(COPY_OUTPUT)/%,%,\
	$(patsubst %/$(LIBNAME).dll,%,\
	$(patsubst %/$(LIBNAME).so,%,\
	$(patsubst %/$(LIBNAME).dylib,%,\
	$(1)))))

$(COPY_TARGET):
	mkdir -p $(COPY_OUTPUT)/$(call extract_rid_from_copy_target,$@)
	cp $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/$(call extract_rid_from_copy_target,$@)/native/$(call extract_filename_from_copy_target,$@) $@

$(foreach f,$(COPY_TARGET),$(f).sha1):
	sha1sum $(patsubst %.sha1,%,$@) > $@

# PublishAOT
AOT_TARGET := $(foreach rid,$(RID),$(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/$(rid)/native/$(LIBNAME).$(call rid_ext_$(rid)))

aot-win-x64: $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/win-x64/native/$(LIBNAME).dll
aot-win-arm64: $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/win-arm64/native/$(LIBNAME).dll
aot-linux-x64: $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/linux-x64/native/$(LIBNAME).so
aot-linux-arm64: $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/linux-arm64/native/$(LIBNAME).so
aot-osx-x64: $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/osx-x64/native/$(LIBNAME).dylib
aot-osx-arm64: $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/osx-arm64/native/$(LIBNAME).dylib

extract_rid_from_aot_target = \
	$(patsubst $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/%/native/$(LIBNAME).dll,%,\
	$(patsubst $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/%/native/$(LIBNAME).so,%,\
	$(patsubst $(SLN)/bin/$(DOTNET_CONFIGURATION)/$(DOTNET_VERSION)/%/native/$(LIBNAME).dylib,%,\
	$(1))))

$(AOT_TARGET): $(SOURCE)
	dotnet publish -r $(call extract_rid_from_aot_target,$@) -c $(DOTNET_CONFIGURATION) $(CSPROJ)