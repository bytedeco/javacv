#include <assert.h>
#include <ARToolKitPlus/arBitFieldPattern.h>
#include <ARToolKitPlus/TrackerMultiMarker.h>
#include <ARToolKitPlus/TrackerSingleMarker.h>

namespace ARToolKitPlus {

static inline void createImagePattern(IDPATTERN nPattern, uint8_t dataPtr[8*8]) {
    uint8_t *p = &dataPtr[8*8-1];
    for (int i = 0; i < 8; i++) {
        *p-- = 0x00; // top border
    }
    *p-- = 0x00; // right border
    assert(pattBits == 6*6);
    for (int i = 0; i<pattBits; i++) {
        *p-- = isBitSet(nPattern, i) ? 0xFF : 0x00;
        if ((p-dataPtr)%8 == 0) {
            *p-- = 0x00; // right border
            *p-- = 0x00; // left border
        }
    }
    *p-- = 0x00; // left border
    for (int i = 0; i < 8; i++) {
        *p-- = 0x00; // bottom border
    }
}

static inline void createImagePatternBCH(int nID, uint8_t dataPtr[8*8]) {
    IDPATTERN nPattern;
    generatePatternBCH(nID, nPattern);
    createImagePattern(nPattern, dataPtr);
}

static inline void createImagePatternSimple(int nID, uint8_t dataPtr[8*8]) {
    IDPATTERN nPattern;
    generatePatternSimple(nID, nPattern);
    createImagePattern(nPattern, dataPtr);
}

#define MAX_PATTERNS 256

class SingleTracker : public TrackerSingleMarker {
public:
    SingleTracker(int width, int height) : TrackerSingleMarker(width, height, MAX_PATTERNS, 6, 6, 6, MAX_PATTERNS) {
        marker_infoTWO = new ARMarkerInfo2[MAX_PATTERNS];
        arImXsize = width;
        arImYsize = height;
    }
};

class MultiTracker : public TrackerMultiMarker {
public:
    MultiTracker(int width, int height) : TrackerMultiMarker(width, height, MAX_PATTERNS, 6, 6, 6, MAX_PATTERNS) {
        marker_infoTWO = new ARMarkerInfo2[MAX_PATTERNS];
        arImXsize = width;
        arImYsize = height;
    }
};

}

