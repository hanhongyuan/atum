package com.oasis.atum.commons.interfaces.api.v2;

import com.oasis.atum.base.infrastructure.service.RedisClient;
import com.oasis.atum.base.infrastructure.util.DateUtil;
import com.oasis.atum.base.infrastructure.util.Restful;
import com.oasis.atum.commons.application.service.CallUpService;
import com.oasis.atum.commons.infrastructure.enums.CallEventState;
import com.oasis.atum.commons.infrastructure.enums.CallState;
import com.oasis.atum.commons.infrastructure.enums.CallType;
import com.oasis.atum.commons.interfaces.dto.MoorDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@AllArgsConstructor
@RequestMapping("v2/7moor")
public class MoorApi
{
	private final RedisClient   redis;
	private final CallUpService service;
	/**
	 * Redis_key 手机绑定关系
	 */
	private static final String REDIS_KEY_BINDING = "binding=>";

	@DeleteMapping("hang-up/{id}")
	public Mono<ResponseEntity> hangUp(@PathVariable final String id)
	{
		log.info("电话挂断 =====> {}", id);

		return service.hangUp(id)
							 .map(v -> Restful.noContent());
	}

	@PostMapping("binding")
	public Mono<ResponseEntity> binding(@RequestBody final Mono<MoorDTO.Binding> data)
	{
		log.info("临时绑定电话关系");

		return data
							 //字段非空
							 .filter(d -> Objects.nonNull(d.call) && Objects.nonNull(d.to))
							 //绑定
							 .flatMap(service::binding)
							 //返回
							 .map(Restful::ok);
	}

	@PostMapping("call-up/back")
	public Mono<ResponseEntity> calUpBack(@RequestBody final Mono<MoorDTO.CallUpCallBack> data)
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
		return redis.getJSONObject(key)
							 //存在返回 200 OK
							 .map(j -> Restful.ok(j.getString("to")))
							 //不存在返回 404 NotFound
							 .defaultIfEmpty(Restful.notFound());
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
																		 @RequestParam(name = "CallState", required = false) final CallEventState callState,
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
		log.info("+---------------------------------------------------------------------------------------------------------------------------+");
		log.info(" CallNo => {}", callNo);
		log.info(" CalledNo => {}", calledNo);
		log.info(" CallSheetId => {}", callSheetID);
		log.info(" CallType => {}", callType);
		log.info(" RingTime => {}", ring);
		log.info(" BeginTime => {}", begin);
		log.info(" EndTime => {}", end);
		log.info(" Queue => {}", queue);
		log.info(" QueueTime => {}", queueTime);
		log.info(" Agent => {}", agent);
		log.info(" AgentName => {}", agentName);
		log.info(" Exten => {}", exten);
		log.info(" State => {}", state);
		log.info(" CallState => {}", callState);
		log.info(" ActionId => {}", actionID);
		log.info(" WebCallActionId => {}", webcallActionID);
		log.info(" RecordFile => {}", recordFile);
		log.info(" FileServer => {}", fileServer);
		log.info(" Province => {}", province);
		log.info(" District => {}", district);
		log.info(" CallId => {}", callID);
		log.info(" IVRkey => {}", IVRKEY);
		log.info(" AccountId => {}", accountId);
		log.info(" AccountName => {}", accountName);
		log.info("+---------------------------------------------------------------------------------------------------------------------------+");

		//时间需要解码
		val charset = "UTF-8";

		return service.hangUp(callNo, calledNo, callType, DateUtil.toDate(URLDecoder.decode(ring, charset)),
				DateUtil.toDate(URLDecoder.decode(begin, charset)), DateUtil.toDate(URLDecoder.decode(end, charset)),
				state, callState, webcallActionID,
				recordFile, fileServer, callID)
							 .map(v -> Restful.ok());
	}
}
