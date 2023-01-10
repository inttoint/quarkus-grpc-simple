package ru.sandbox.quarkus;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import ru.sandbox.quarkus.proto.Visitor;
import ru.sandbox.quarkus.proto.VisitorList;
import ru.sandbox.quarkus.proto.VisitorService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ru.sandbox.quarkus.TimestampUtils.now;

@GrpcService
public class VisitorGrpcService implements VisitorService {

    private static final List<Visitor> VISITORS = new ArrayList<>();

    static {
        VISITORS.add(Visitor.newBuilder().setId(1L).setName("First").setDate(now()).build());
        VISITORS.add(Visitor.newBuilder().setId(2L).setName("Second").setDate(now()).build());
        VISITORS.add(Visitor.newBuilder().setId(3L).setName("Third").setDate(now()).build());
    }

    @Override
    public Uni<Visitor> create(Visitor request) {
        return Uni.createFrom().item(request).invoke(VISITORS::add);
    }

    @Override
    public Uni<Visitor> findById(Int64Value request) {
        Visitor visitor = VISITORS.stream().filter(v -> v.getId() == request.getValue()).findFirst().orElse(null);
        return Uni.createFrom().item(visitor);
    }

    @Override
    public Uni<VisitorList> findAll(Empty request) {
        VisitorList visitorList = VisitorList.newBuilder().addAllVisitors(VISITORS).setCount(VISITORS.size()).build();
        return Uni.createFrom().item(visitorList);
    }

    @Override
    public Uni<BoolValue> delete(Int64Value request) {
        Boolean success = VISITORS.stream()
                .filter(v -> v.getId() == request.getValue())
                .findFirst()
                .map(VISITORS::remove).orElse(false);
        return Uni.createFrom().item(BoolValue.of(success));
    }

    @Override
    public Multi<VisitorList> streamAll(Int32Value count) {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(1))
                .select()
                .first(count.getValue())
                .map(tick -> (tick.intValue() >= VISITORS.size())
                        ? VisitorList.newBuilder().setCount(0L).build()
                        : VisitorList.newBuilder().addAllVisitors(VISITORS.subList(tick.intValue(), tick.intValue() + 1)).setCount(1L).build());
    }
}
