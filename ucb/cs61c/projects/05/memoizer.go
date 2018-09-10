package memoizer

import (
	"hash/crc64"

	"github.com/61c-teach/sp18-proj5"
)

var noResultError = proj5.CreateMemErr(proj5.MemErr_serCrash, "Internal error, classifier crashed and no cached result available.", nil)
var classError = proj5.CreateMemErr(proj5.MemErr_serErr, "", nil)
var classRespIDError = proj5.CreateMemErr(proj5.MemErr_serCorrupt, "Internal error, classifier returned unexpected ID", nil)

/* The simplest possible implementation that does anything interesting.
This doesn't even do memoization, it just proxies requests between the client
and the classifier. You will need to improve this to use the cache effectively. */
func Memoizer(memHandle proj5.MnistHandle, classHandle proj5.MnistHandle, cacheHandle proj5.CacheHandle) {
	defer close(memHandle.RespQ)
	defer func() {
		select {
		case <-classHandle.RespQ:
			return
		default:
			close(classHandle.RespQ)
			return
		}
	}()
	defer func() {
		select {
		case <-cacheHandle.RespQ:
			return
		default:
			close(cacheHandle.RespQ)
			return
		}
	}()

	cacheOk := true
	classOk := true
	seenClassRespIds := make(map[int64]struct{})

	crc64Table := crc64.MakeTable(0xC96C5795D7870F42)
	for req := range memHandle.ReqQ {
		goodResult := false
		id := req.Id
		key := crc64.Checksum(req.Val, crc64Table)

		if cacheOk {
			cacheHandle.ReqQ <- proj5.CacheReq{false, key, 0, id}
			cacheResp, ok := <-cacheHandle.RespQ
			if ok {
				if cacheResp.Exists && cacheResp.Id == id {
					goodResult = true
					memHandle.RespQ <- proj5.MnistResp{cacheResp.Val, id, nil}
				}
			} else {
				cacheOk = false
			}
		}
		if !goodResult {
			if classOk {
				classHandle.ReqQ <- req
				if classResp, ok := <-classHandle.RespQ; ok {
					if classResp.Err != nil {
						memHandle.RespQ <- proj5.MnistResp{0, id, classError}
					} else if _, exist := seenClassRespIds[classResp.Id]; exist {
						memHandle.RespQ <- proj5.MnistResp{0, id, classRespIDError}
					} else if classResp.Id != id {
						memHandle.RespQ <- proj5.MnistResp{0, id, classRespIDError}
					} else {
						seenClassRespIds[classResp.Id] = struct{}{}
						if cacheOk {
							cacheHandle.ReqQ <- proj5.CacheReq{true, key, classResp.Val, 0}
						}
						memHandle.RespQ <- classResp
					}
				} else {
					classOk = false
					memHandle.RespQ <- proj5.MnistResp{0, id, noResultError}
				}
			} else {
				memHandle.RespQ <- proj5.MnistResp{0, id, noResultError}
			}
		}
	}
}
