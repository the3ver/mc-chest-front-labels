package net.frank.chestfrontlabels;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

// Beschriftung einer Truhe: hängt als Attachment am Block-Entity, wird gespeichert und an alle Clients synchronisiert.
// Schleichen + Rechtsklick mit leerer Hand: Server öffnet beim Spieler den Editor, der Client schickt den Text zurück.
public final class ChestLabels {

    public static final AttachmentType<String> LABEL = AttachmentRegistry.create(
            ChestFrontLabelsMod.id("label"),
            builder -> builder.persistent(Codec.STRING)
                    .syncWith(ByteBufCodecs.STRING_UTF8, AttachmentSyncPredicate.all()));

    public record OpenEditorPayload(BlockPos pos, String label) implements CustomPacketPayload {
        public static final Type<OpenEditorPayload> TYPE = new Type<>(ChestFrontLabelsMod.id("open_editor"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenEditorPayload> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                OpenEditorPayload::pos,
                ByteBufCodecs.stringUtf8(ChestLabelLogic.MAX_LENGTH * 4),
                OpenEditorPayload::label,
                OpenEditorPayload::new);

        @Override
        public Type<OpenEditorPayload> type() {
            return TYPE;
        }
    }

    public record SetLabelPayload(BlockPos pos, String label) implements CustomPacketPayload {
        public static final Type<SetLabelPayload> TYPE = new Type<>(ChestFrontLabelsMod.id("set_label"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SetLabelPayload> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                SetLabelPayload::pos,
                ByteBufCodecs.stringUtf8(ChestLabelLogic.MAX_LENGTH * 4),
                SetLabelPayload::label,
                SetLabelPayload::new);

        @Override
        public Type<SetLabelPayload> type() {
            return TYPE;
        }
    }

    private ChestLabels() {}

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(OpenEditorPayload.TYPE, OpenEditorPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetLabelPayload.TYPE, SetLabelPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                SetLabelPayload.TYPE, (payload, context) -> setLabel(context.player(), payload.pos(), payload.label()));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            if (!wantsToEdit(player, hand) || !(level.getBlockEntity(pos) instanceof ChestBlockEntity chest)) {
                return InteractionResult.PASS;
            }
            if (player instanceof ServerPlayer serverPlayer
                    && ServerPlayNetworking.canSend(serverPlayer, OpenEditorPayload.TYPE)) {
                ServerPlayNetworking.send(serverPlayer, new OpenEditorPayload(pos, labelOf(chest)));
            }
            // Auf beiden Seiten SUCCESS, damit die Truhe nicht aufgeht
            return InteractionResult.SUCCESS;
        });
    }

    private static boolean wantsToEdit(Player player, InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND
                && player.isSecondaryUseActive()
                && player.getMainHandItem().isEmpty()
                && player.getOffhandItem().isEmpty()
                && player.mayBuild();
    }

    private static void setLabel(ServerPlayer player, BlockPos pos, String raw) {
        if (!player.mayBuild() || !player.isWithinBlockInteractionRange(pos, 1.0)) return;
        if (!(player.level().getBlockEntity(pos) instanceof ChestBlockEntity chest)) return;

        String label = ChestLabelLogic.sanitize(raw);
        applyLabel(chest, label);
        // Beide Hälften tragen den Text, damit er nach dem Abbauen einer Hälfte an der anderen bleibt
        partner(chest).ifPresent(other -> applyLabel(other, label));
    }

    // Angezeigte Beschriftung, bei Doppeltruhen die gemeinsame. Leer ohne Beschriftung.
    public static String labelOf(ChestBlockEntity chest) {
        String own = chest.getAttachedOrElse(LABEL, "");
        Optional<ChestBlockEntity> other = partner(chest);
        if (other.isEmpty()) return own;
        String otherLabel = other.get().getAttachedOrElse(LABEL, "");
        boolean isLeft = chest.getBlockState().getValue(ChestBlock.TYPE) == ChestType.LEFT;
        return isLeft ? ChestLabelLogic.combine(own, otherLabel) : ChestLabelLogic.combine(otherLabel, own);
    }

    // Andere Hälfte einer Doppeltruhe
    private static Optional<ChestBlockEntity> partner(ChestBlockEntity chest) {
        var state = chest.getBlockState();
        if (chest.getLevel() == null
                || !state.hasProperty(ChestBlock.TYPE)
                || state.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
            return Optional.empty();
        }
        BlockPos otherPos = ChestBlock.getConnectedBlockPos(chest.getBlockPos(), state);
        return chest.getLevel().getBlockEntity(otherPos) instanceof ChestBlockEntity other
                ? Optional.of(other)
                : Optional.empty();
    }

    private static void applyLabel(ChestBlockEntity chest, String label) {
        if (label.isEmpty()) {
            chest.removeAttached(LABEL);
        } else {
            chest.setAttached(LABEL, label);
        }
        chest.setChanged();
    }
}
