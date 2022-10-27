/* MVVMBenchmark.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 10 12:37:14 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.benchmark;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.zkoss.bind.BindComposer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.Form;
import org.zkoss.bind.PhaseListener;
import org.zkoss.bind.Validator;
import org.zkoss.bind.impl.BindContextUtil;
import org.zkoss.bind.impl.BindEvaluatorXUtil;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.bind.sys.TemplateResolver;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.bind.sys.debugger.BindingAnnotationInfoChecker;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.lang.Classes;
import org.zkoss.xel.ExpressionX;
import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.stateless.ui.util.ObjectMappers;
import org.zkoss.clientbind.ClientBinder;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A MVVM benchmark for client bind
 *
 * @author jameschu
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class MVVMBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(MVVMBenchmark.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}

	private StatelessTestBase setup;
	private Binder binder;

	@Setup
	public void setUp() {
		setup = new StatelessTestBase(false);
		setup.beforeEach();

		TestVM view = new TestVM();
		view.init();
		AbstractComponent dummy = new AbstractComponent();
		dummy.setAttribute(BindComposer.VM_ID, "vm");
		binder = new DummyBinder(dummy, view);
		prepareData();
		bindContext = BindContextUtil.newBindContext(binder, null, false, null, binder.getView(), null);
		evalx = BindEvaluatorXUtil.createEvaluator(null);
	}
	private BindContext bindContext;
	private BindEvaluatorX evalx;

	@TearDown
	public void tearDown() {
		setup.afterEach();
	}

//	@Benchmark
	public String timeOfEvalCollection() {
		ExpressionX expressionX = evalx.parseExpressionX(null, "vm.persons[5]", Object.class);
		return String.valueOf(evalx.getValue(bindContext, binder.getView(), expressionX));
	}

//	@Benchmark
	public String timeOfEvalItemInCollection() {
		ExpressionX expressionX = evalx.parseExpressionX(null, "vm.persons[5].firstName", Object.class);
		return String.valueOf(evalx.getValue(bindContext, binder.getView(), expressionX));
	}

//	@Benchmark
	public String timeOfUpdateCollection() {
		ExpressionX expressionX = evalx.parseExpressionX(null, "vm.persons[4].firstName", Object.class);
		evalx.setValue(bindContext, binder.getView(), expressionX, "new");
		return "";
	}

	@Benchmark
	public String timeOfPureUpdateData() {
		((List<Person>) tmpIdMapping.get("persons")).get(5).setFirstName("new");
		return "";
	}

//	@Benchmark
	public String timeOfGetDataFromCache() {
		return ((List<Person>) tmpIdMapping.get("persons")).get(5).getFirstName();
	}

//	@Benchmark
	public String timeOfUpdateDataFromCache() {
		Person person = ((List<Person>) tmpIdMapping.get("persons")).get(5);
		ObjectReader objectReader = ObjectMappers.SETTER_OBJECT_MAPPER.readerForUpdating(person);
		JSONObject pojo = new JSONObject();
		pojo.put("$id", "testId");
		pojo.put("lastName", "new");
		String encoded = JSONValue.toJSONString(pojo);
		String newEncoded = null;
		try {
			Object o = objectReader.readValue(encoded);
			newEncoded = objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (!encoded.equals(newEncoded)) {
//				Clients.sendClientCommand(view, command, value);
		}
		return newEncoded;
	}

	private Map<String, Object> tmpIdMapping = new HashMap<>();

	private void prepareData() {
		tmpIdMapping = new HashMap<>();
		initObjectMapper();
		Object viewModel = binder.getViewModel();
		try {
			objectMapper.writeValueAsString(viewModel);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		tmpIdMapping.put("persons", ((TestVM) viewModel).getPersons());
	}

	public static class DummyBinder implements ClientBinder {
		private Component _view;
		private Object _viewModel;

		public DummyBinder(Component view, Object viewModel) {
			_view = view;
			_viewModel = viewModel;
		}

		@Override
		public boolean doValidate(Component comp, SavePropertyBinding binding) {
			return false;
		}

		@Override
		public boolean doValidate(Component comp, SaveFormBinding binding) {
			return false;
		}

		@Override
		public void init(Component root, Object viewModel) {

		}

		@Override
		public void init(Component root, Object viewModel, Map<String, Object> initArgs) {

		}

		@Override
		public void destroy(Component root, Object viewModel) {

		}

		@Override
		public void loadComponent(Component comp, boolean loadinit) {

		}

		@Override
		public BindEvaluatorX getEvaluatorX() {
			return null;
		}

		@Override
		public void addCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> commandArgs) {

		}

		@Override
		public void addGlobalCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> commandArgs) {

		}

		@Override
		public void setTemplate(Component comp, String attr, String templateExpr, Map<String, Object> templateArgs) {

		}

		@Override
		public void addPropertyInitBinding(Component comp, String attr, String initExpr, Map<String, Object> initArgs, String converterExpr, Map<String, Object> converterArgs) {

		}

		@Override
		public void addPropertyLoadBindings(Component comp, String attr, String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {

		}

		@Override
		public void addPropertySaveBindings(Component comp, String attr, String saveExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs, String validatorExpr, Map<String, Object> validatorArgs) {

		}

		@Override
		public void addFormInitBinding(Component comp, String id, String initExpr, Map<String, Object> initArgs) {

		}

		@Override
		public void addFormLoadBindings(Component comp, String id, String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs) {

		}

		@Override
		public void addFormSaveBindings(Component comp, String id, String saveExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs, String validatorExpr, Map<String, Object> validatorArgs) {

		}

		@Override
		public void addChildrenInitBinding(Component comp, String initExpr, Map<String, Object> initArgs) {

		}

		@Override
		public void addChildrenInitBinding(Component comp, String initExpr, Map<String, Object> initArgs, String converterExpr, Map<String, Object> converterArgs) {

		}

		@Override
		public void addChildrenLoadBindings(Component comp, String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs) {

		}

		@Override
		public void addChildrenLoadBindings(Component comp, String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs, String converterExpr, Map<String, Object> converterArgs) {

		}

		@Override
		public void addReferenceBinding(Component comp, String attr, String loadExpr, Map<String, Object> bindingArgs) {

		}

		@Override
		public void removeBindings(Component comp) {

		}

		@Override
		public void removeBindings(Set<Component> comps) {

		}

		@Override
		public void removeBindings(Component comp, String key) {

		}

		@Override
		public Converter getConverter(String name) {
			return null;
		}

		@Override
		public Validator getValidator(String name) {
			return null;
		}

		@Override
		public void notifyChange(Object bean, String property) {

		}

		@Override
		public int sendCommand(String command, Map<String, Object> args) {
			return 0;
		}

		@Override
		public void postCommand(String command, Map<String, Object> args) {

		}

		@Override
		public Object getViewModel() {
			return _viewModel;
		}

		@Override
		public void setViewModel(Object viewModel) {

		}

		@Override
		public void addFormAssociatedSaveBinding(Component associatedComp, String formId, SaveBinding saveBinding, String fieldName) {

		}

		@Override
		public Set<SaveBinding> getFormAssociatedSaveBindings(Component formComp) {
			return null;
		}

		@Override
		public void storeForm(Component comp, String id, Form form) {

		}

		@Override
		public Form getForm(Component comp, String id) {
			return null;
		}

		@Override
		public Tracker getTracker() {
			return null;
		}

		@Override
		public ValidationMessages getValidationMessages() {
			return null;
		}

		@Override
		public void setValidationMessages(ValidationMessages messages) {

		}

		@Override
		public boolean hasValidator(Component comp, String attr) {
			return false;
		}

		@Override
		public TemplateResolver getTemplateResolver(Component comp, String attr) {
			return null;
		}

		@Override
		public List<Binding> getLoadPromptBindings(Component comp, String attr) {
			return null;
		}

		@Override
		public PhaseListener getPhaseListener() {
			return null;
		}

		@Override
		public List<PhaseListener> getPhaseListeners() {
			return null;
		}

		@Override
		public void setPhaseListener(PhaseListener listener) {

		}

		@Override
		public void addPhaseListener(PhaseListener listener) {

		}

		@Override
		public boolean isActivating() {
			return false;
		}

		@Override
		public void initQueue() {

		}

		@Override
		public void initActivator() {

		}

		@Override
		public BindingExecutionInfoCollector getBindingExecutionInfoCollector() {
			return null;
		}

		@Override
		public BindingAnnotationInfoChecker getBindingAnnotationInfoChecker() {
			return null;
		}

		@Override
		public String getQueueName() {
			return null;
		}

		@Override
		public String getQueueScope() {
			return null;
		}

		@Override
		public void addSaveFormFieldName(Form form, String fieldName) {

		}

		@Override
		public void addSaveFormFieldName(Form form, Set<String> fieldNames) {

		}

		@Override
		public Set<String> getSaveFormFieldNames(Form self) {
			return null;
		}

		@Override
		public Set<String> removeSaveFormFieldNames(Form self) {
			return null;
		}

		@Override
		public Map<String, Method> getMatchMediaValue() {
			return null;
		}

		@Override
		public Component getView() {
			return _view;
		}
	}

	private ObjectMapper objectMapper;

	private void initObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.registerModule(new SimpleModule() {

			public void setupModule(SetupContext context) {
				super.setupModule(context);
				this._serializers = this._serializers == null ? new SimpleSerializers() : this._serializers;
				this._serializers.addSerializer(Date.class, new JsonSerializer<Date>() {
					public void serialize(Date value, JsonGenerator gen,
										  SerializerProvider serializers) throws IOException {
						gen.writeStartObject();
						// type
						gen.writeStringField("_@t", "date");
						// raw value as timestamp
						gen.writeNumberField("_@n", value.getTime());
						// string value as the same as Java's text, see LoadPropertyBindingImpl#load()
						gen.writeStringField("_@s", (String) Classes.coerce(String.class, value));
						gen.writeEndObject();
					}
				});

				this._serializers.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
					public void serialize(LocalDate value, JsonGenerator gen,
										  SerializerProvider serializers) throws IOException {
						gen.writeStartObject();
						// type
						gen.writeStringField("_@t", "date");
						// raw value as timestamp
						gen.writeNumberField("_@n", value.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
						// string value as the same as Java's text, see LoadPropertyBindingImpl#load()
						gen.writeStringField("_@s", (String) Classes.coerce(String.class, value));
						gen.writeEndObject();
					}
				});

				this._serializers.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
					public void serialize(LocalDateTime value, JsonGenerator gen,
										  SerializerProvider serializers) throws IOException {
						gen.writeStartObject();
						// type
						gen.writeStringField("_@t", "date");
						// raw value as timestamp
						gen.writeNumberField("_@n", value.toInstant(ZoneOffset.UTC).toEpochMilli());
						// string value as the same as Java's text, see LoadPropertyBindingImpl#load()
						gen.writeStringField("_@s", (String) Classes.coerce(String.class, value));
						gen.writeEndObject();
					}
				});

				this._serializers.addSerializer(LocalTime.class, new JsonSerializer<LocalTime>() {
					public void serialize(LocalTime value, JsonGenerator gen,
										  SerializerProvider serializers) throws IOException {
						gen.writeStartObject();
						// type
						gen.writeStringField("_@t", "date");
						// raw value as timestamp
						gen.writeNumberField("_@n", value.atDate(LocalDate.now()).toInstant(ZoneOffset.UTC).toEpochMilli());
						// string value as the same as Java's text, see LoadPropertyBindingImpl#load()
						gen.writeStringField("_@s", (String) Classes.coerce(String.class, value));
						gen.writeEndObject();
					}
				});

				this._serializers.addSerializer(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
					public void serialize(ZonedDateTime value, JsonGenerator gen,
										  SerializerProvider serializers) throws IOException {
						gen.writeStartObject();
						// type
						gen.writeStringField("_@t", "date");
						// raw value as timestamp
						gen.writeNumberField("_@n", value.toInstant().toEpochMilli());
						// string value as the same as Java's text, see LoadPropertyBindingImpl#load()
						gen.writeStringField("_@s", (String) Classes.coerce(String.class, value));
						gen.writeEndObject();
					}
				});

				context.addSerializers(this._serializers);
				context.addBeanSerializerModifier(new BeanSerializerModifier() {

					public JsonSerializer<?> modifySerializer(
							SerializationConfig config,
							BeanDescription beanDesc,
							JsonSerializer<?> serializer) {
						if (serializer instanceof BeanSerializerBase) {
							ExtraFieldSerializer extraFieldSerializer = new ExtraFieldSerializer(
									(BeanSerializerBase) serializer);
//							extraFieldSerializer.owner = ClientBindComposer.this;
							return extraFieldSerializer;
						}
						return serializer;

					}

					@Override
					public JsonSerializer<?> modifyArraySerializer(SerializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
						return super.modifyArraySerializer(config, valueType, beanDesc, serializer);
					}

					@Override
					public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
						if (serializer instanceof CollectionSerializer) {
							ExtraCollectionValueSerializer extraCollectionValueSerializer = new ExtraCollectionValueSerializer((CollectionSerializer) serializer);
//							extraCollectionValueSerializer.owner = ClientBindComposer.this;
							return extraCollectionValueSerializer;
						}
						return serializer;
					}

					@Override
					public JsonSerializer<?> modifyMapSerializer(SerializationConfig config, MapType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
						if (serializer instanceof MapSerializer) {
							ExtraMapEntrySerializer extraEntrySerializer = new ExtraMapEntrySerializer((MapSerializer) serializer);
//							extraEntrySerializer.owner = ClientBindComposer.this;
							return extraEntrySerializer;
						}
						return serializer;
					}
				});

			}
		});
	}
}