#ifdef _WIN32
#define first_protocol   first_protocol_bad
#define url_interrupt_cb url_interrupt_cb_bad
#define first_iformat    first_iformat_bad
#define first_oformat    first_oformat_bad
#endif

#include <libavformat/avformat.h>

#if LIBAVFORMAT_VERSION_MINOR >= 67
    #define URL_WRITE_BUF_TYPE const unsigned char *
#else
    #define URL_WRITE_BUF_TYPE unsigned char *
#endif

#ifdef _WIN32
#undef first_protocol
__declspec(dllimport) extern URLProtocol *first_protocol;

#undef url_interrupt_cb
__declspec(dllimport) extern URLInterruptCB *url_interrupt_cb;

#undef first_iformat
__declspec(dllimport) extern AVInputFormat *first_iformat;

#undef first_oformat
__declspec(dllimport) extern AVOutputFormat *first_oformat;
#endif
