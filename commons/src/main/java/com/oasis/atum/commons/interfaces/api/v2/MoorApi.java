package com.oasis.atum.commons.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.commons.application.service.CallUpService;
import com.oasis.atum.commons.domain.enums.CallState;
import com.oasis.atum.commons.domain.enums.CallType;
import com.oasis.atum.commons.interfaces.dto.CallUpDTO;
import com.oasis.atum.commons.interfaces.request.BindingRequest;
import com.oasis.atum.commons.interfaces.request.CallUpCallBack;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * 打电话接口
 * 第三方 => 容联七陌
 */
@Slf4j
@RestController
@RequestMapping("v2/7moor")
public class MoorApi
{
	private final RedisClient   redis;
	private final CallUpService service;
	/**
	 * Redis_key 手机绑定关系
	 */
	private static final String REDIS_KEY_BINDING = "binding=>";

	public MoorApi(final RedisClient redis, final CallUpService service)
	{
		this.redis = redis;
		this.service = service;
	}

	@PutMapping("binding")
	public Mono<ResponseEntity> binding(@RequestBody final Mono<BindingRequest> data)
	{
		log.info("临时绑定电话关系 =====> {}", data);

		return data
						 //字段非空
						 .filter(d -> Objects.nonNull(d.call) && Objects.nonNull(d.to))
						 //暂存Redis 30分钟
						 .flatMap(d -> redis.put(REDIS_KEY_BINDING + d.call, d.to, 30L)
														 //200
														 .map(Restful::ok));
	}

	@DeleteMapping("unbinding")
	public Mono<ResponseEntity> unbinding(@RequestBody final Mono<BindingRequest> data)
	{
		log.info("解除绑定关系 =====> {}", data);
		return data
						 //非空
						 .filter(d -> Objects.nonNull(d.call))
						 .map(d -> REDIS_KEY_BINDING + d.call)
						 //key是否存在
						 .flatMap(s -> redis.exists(s)
														 .filter(k -> k)
														 //存在删除
														 .flatMap(b -> redis.delete(s)))
						 .map(l -> Restful.noContent());
	}

	@PostMapping("call-up")
	public Mono<ResponseEntity> callUp(@RequestBody final Mono<CallUpDTO> data)
	{
		log.info("打电话");
		return data.flatMap(service::callUp)
						 .map(s -> Restful.ok("actionId", s));
	}

	@PostMapping("call-up/back")
	public Mono<ResponseEntity> calUpBack(@RequestBody final Mono<CallUpCallBack> data)
	{
		log.info("打电话回调");

		return data
						 .flatMap(service::updateCallUp)
						 .map(v -> Restful.ok());
	}

	@GetMapping(params = "mobile")
	public Mono<ResponseEntity> callUpTo(@RequestParam String mobile)
	{
		log.info("容联七陌回调 =====> {}", mobile);
		//从Redis获取绑定关系
		val key = REDIS_KEY_BINDING + mobile;
		return redis.exists(key)
						 //存在删除
						 .flatMap(b -> redis.delete(key))
						 //200 return key
						 .map(b -> Restful.ok(key));
	}

	/**
	 * 通话事件推送更新回调
	 * @return
	 */
	@GetMapping("hang-up")
	@SneakyThrows(UnsupportedEncodingException.class)
	public Mono<ResponseEntity> hangUp(@RequestParam(name = "CallNo") final String callNo, @RequestParam(name = "CalledNo") final String calledNo,
																		 @RequestParam(name = "CallSheetID", required = false) final String callSheetID,
																		 @RequestParam(name = "CallType", required = false) final CallType callType,
																		 @RequestParam(name = "Ring", required = false) final String ring,
																		 @RequestParam(name = "Begin", required = false) final String begin,
																		 @RequestParam(name = "End", required = false) final String end,
																		 @RequestParam(name = "QueueTime", required = false) final String queueTime,
																		 @RequestParam(name = "Agent", required = false) final String agent,
																		 @RequestParam(name = "Exten", required = false) final String exten,
																		 @RequestParam(name = "AgentName", required = false) final String agentName,
																		 @RequestParam(name = "Queue", required = false) final String queue,
																		 @RequestParam(name = "State", required = false) final CallState state,
																		 @RequestParam(name = "CallState", required = false) final String callState,
																		 @RequestParam(name = "ActionID", required = false) final String actionID,
																		 @RequestParam(name = "WebcallActionID", required = false) final String webcallActionID,
																		 @RequestParam(name = "RecordFile", required = false) final String recordFile,
																		 @RequestParam(name = "FileServer", required = false) final String fileServer,
																		 @RequestParam(name = "Province", required = false) final String province,
																		 @RequestParam(name = "District", required = false) final String district,
																		 @RequestParam(name = "CallID", required = false) final String callID,
																		 @RequestParam(name = "IVRKEY", required = false) final String IVRKEY,
																		 @RequestParam(name = "AccountId", required = false) final String accountId,
																		 @RequestParam(name = "AccountName", required = false) final String accountName)
	{
		log.info("通话事件推送更新回调");
		log.info(callNo);
		log.info(calledNo);
		log.info(callSheetID);
		log.info(callType + "");
		log.info(ring);
		log.info(begin);
		log.info(end);
		log.info(queueTime);
		log.info(agent);
		log.info(exten);
		log.info(agentName);
		log.info(queue);
		log.info(state + "");
		log.info(callState);
		log.info(actionID);
		log.info(webcallActionID);
		log.info(recordFile);
		log.info(fileServer);
		log.info(province);
		log.info(district);
		log.info(callID);
		log.info(IVRKEY);
		log.info(accountId);
		log.info(accountName);

		//时间需要解码
		val charset = "UTF-8";

		return service.hangUp(callNo, calledNo, callType, DateUtil.toDate(URLDecoder.decode(ring, charset)),
			DateUtil.toDate(URLDecoder.decode(begin, charset)), DateUtil.toDate(URLDecoder.decode(end, charset)),
			state, webcallActionID,
			recordFile, fileServer)
						 .map(v -> Restful.ok());
	}
}
