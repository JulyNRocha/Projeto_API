package com.dev.projetoAPI.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dev.projetoAPI.controller.dto.DataStreamDto;
import com.dev.projetoAPI.controller.dto.ItemizedDataStreamDto;
import com.dev.projetoAPI.controller.form.DataStreamForm;
import com.dev.projetoAPI.model.DataStream;
import com.dev.projetoAPI.model.SensorDevice;
import com.dev.projetoAPI.repository.DataStreamRepository;
import com.dev.projetoAPI.repository.MeasurementUnitRepository;
import com.dev.projetoAPI.repository.SensorDeviceRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags="Streams de Dados (DataStream)", produces="application/json")
@RestController
public class DataStreamController {
	
	@Autowired
	private DataStreamRepository dataStreamRepository;
	
	@Autowired
	private SensorDeviceRepository sensorDeviceRepository;
	
	@Autowired
	private MeasurementUnitRepository unitRepository;
	
	
	@ApiOperation(value = "Consulta de dados de um Stream (DataStream) específico apartirar de sua Key")
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Retorno do (DataStream) específico"),
		    @ApiResponse(code = 404, message = "Stream de dados (DataStream) com essa Key não foi encontrado"),
		    @ApiResponse(code = 500, message = "Erro interno"),
		})
	@GetMapping("DataStream/{key}")
	public ResponseEntity<ItemizedDataStreamDto> getdDataStreamById(@PathVariable String key) {
		
		Optional<DataStream> dataStream = dataStreamRepository.findByStreamKey(key);
		
		if(dataStream.isPresent()) {
			return ResponseEntity.ok(new ItemizedDataStreamDto(dataStream.get()));
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@ApiOperation(value = "Registrar Stream (DataStream) para Dispositivo (SensorDevice) apartirar da Key do Dispositivo")
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Registro de Stream (DataStream) criado com sucesso"),
		    @ApiResponse(code = 404, message = "Dispositivo (SensorDevice) com essa Key não foi encontrado"),
		    @ApiResponse(code = 500, message = "Erro interno"),
		})
	@PostMapping(value = "/SensorDevice/{key}/DataStream", consumes="application/json" )
	public ResponseEntity<DataStreamDto> register(@PathVariable String key,@RequestBody DataStreamForm form, UriComponentsBuilder uriBuilder) {
		
		Optional<SensorDevice> device = sensorDeviceRepository.findByDevicekey(key);
		if(device.isPresent()) {
			DataStream dataStream = form.converter(unitRepository, device.get());
			dataStreamRepository.save(dataStream);
			
			URI uri = uriBuilder.path("/DataStream/{id}").buildAndExpand(dataStream.getId()).toUri();
			return ResponseEntity.created(uri).body(new DataStreamDto(dataStream));
		}		
		
		return ResponseEntity.ok(null);
	}	
	
	
}
