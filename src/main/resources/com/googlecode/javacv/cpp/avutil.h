#ifdef _WIN32
#define av_pix_fmt_descriptors av_pix_fmt_descriptors_bad
#define av_sha1_size           av_sha1_size_bad
#define av_md5_size            av_md5_size_bad
#endif

#include <libavutil/avutil.h>
#include <libavutil/adler32.h>
#include <libavutil/avstring.h>
#include <libavutil/base64.h>
#include <libavutil/crc.h>
#include <libavutil/fifo.h>
#include <libavutil/lzo.h>
#include <libavutil/md5.h>
#include <libavutil/pixdesc.h>
#include <libavutil/sha1.h>

#ifdef _WIN32
#undef av_pix_fmt_descriptors
__declspec(dllimport) extern const AVPixFmtDescriptor av_pix_fmt_descriptors[];

#undef av_sha1_size
__declspec(dllimport) extern const int av_sha1_size;

#undef av_md5_size
__declspec(dllimport) extern const int av_md5_size;
#endif
