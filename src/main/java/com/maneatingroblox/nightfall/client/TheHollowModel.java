package com.maneatingroblox.nightfall.client;

import com.maneatingroblox.nightfall.Nightfall;
import com.maneatingroblox.nightfall.entity.TheHollowEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** A deliberately narrow, long-limbed silhouette; it should not read as a zombie in different clothes. */
public final class TheHollowModel extends HierarchicalModel<TheHollowEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Nightfall.MOD_ID, "the_hollow"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart shroud;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public TheHollowModel(ModelPart root) {
        this.root = root;
        this.torso = root.getChild("torso");
        this.head = this.torso.getChild("head");
        this.shroud = this.torso.getChild("shroud");
        this.rightArm = this.torso.getChild("right_arm");
        this.leftArm = this.torso.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition torso = root.addOrReplaceChild("torso", CubeListBuilder.create()
                        // A pinched ribcage under an oversized, ragged shoulder mantle.
                        .texOffs(0, 30).addBox(-3.0F, -1.0F, -1.5F, 6.0F, 14.0F, 3.0F)
                        .texOffs(20, 30).addBox(-4.5F, -0.5F, -1.8F, 9.0F, 5.0F, 4.0F,
                                new CubeDeformation(0.12F))
                        // Exposed, uneven ribs break up the ordinary humanoid chest shape.
                        .texOffs(20, 42).addBox(-2.5F, 3.0F, -1.85F, 5.0F, 0.55F, 0.45F)
                        .texOffs(32, 42).addBox(-2.2F, 5.0F, -1.85F, 4.4F, 0.55F, 0.45F)
                        .texOffs(44, 42).addBox(-1.9F, 7.0F, -1.85F, 3.8F, 0.55F, 0.45F),
                PartPose.ZERO);

        torso.addOrReplaceChild("head", CubeListBuilder.create()
                        // Narrow cranium, drooping hood, and a blank mask instead of a human face.
                        .texOffs(0, 0).addBox(-3.0F, -9.0F, -3.0F, 6.0F, 9.0F, 6.0F)
                        .texOffs(28, 0).addBox(-4.5F, -9.2F, -3.4F, 9.0F, 4.0F, 7.0F,
                                new CubeDeformation(0.16F))
                        .texOffs(18, 17).addBox(-2.25F, -6.4F, -3.32F, 4.5F, 5.2F, 0.28F)
                        // Frayed strips hang from the hood rather than forming ears or horns.
                        .texOffs(40, 14).addBox(-4.0F, -6.0F, 0.4F, 1.0F, 5.5F, 1.0F)
                        .texOffs(46, 14).addBox(3.0F, -5.0F, 0.4F, 1.0F, 4.5F, 1.0F),
                PartPose.ZERO);

        torso.addOrReplaceChild("shroud", CubeListBuilder.create()
                        // A torn back-shroud adds a ragged outline behind the arms and legs.
                        .texOffs(32, 12).addBox(-4.5F, 1.0F, 1.4F, 9.0F, 12.0F, 0.9F)
                        .texOffs(32, 26).addBox(-4.5F, 10.0F, 1.2F, 2.2F, 7.0F, 0.8F)
                        .texOffs(44, 26).addBox(2.3F, 9.0F, 1.2F, 2.2F, 8.0F, 0.8F)
                        .texOffs(56, 26).addBox(-1.0F, 12.0F, 1.2F, 1.4F, 5.0F, 0.8F),
                PartPose.ZERO);

        CubeListBuilder rightArm = longArm(0, 48);
        CubeListBuilder leftArm = longArm(0, 48);
        torso.addOrReplaceChild("right_arm", rightArm, PartPose.offset(-3.7F, 1.0F, 0.0F));
        torso.addOrReplaceChild("left_arm", leftArm, PartPose.offset(3.7F, 1.0F, 0.0F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(38, 46).addBox(-1.25F, 0.0F, -1.15F, 2.5F, 11.0F, 2.3F)
                        .texOffs(16, 48).addBox(-1.55F, 9.0F, -1.8F, 3.1F, 2.2F, 3.5F),
                PartPose.offset(-1.45F, 11.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(50, 46).addBox(-1.25F, 0.0F, -1.15F, 2.5F, 11.0F, 2.3F)
                        .texOffs(16, 48).addBox(-1.55F, 9.0F, -1.8F, 3.1F, 2.2F, 3.5F),
                PartPose.offset(1.45F, 11.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    private static CubeListBuilder longArm(int u, int v) {
        return CubeListBuilder.create()
                .texOffs(u, v).addBox(-1.15F, -1.0F, -1.1F, 2.3F, 9.5F, 2.2F)
                .texOffs(u + 10, v).addBox(-1.0F, 7.5F, -1.0F, 2.0F, 7.5F, 2.0F)
                .texOffs(u + 20, v).addBox(-1.1F, 14.0F, -1.0F, 2.2F, 2.0F, 2.2F)
                // Two tapered-looking, separated finger bones trail below the wrist.
                .texOffs(u + 30, v).addBox(-0.95F, 15.0F, -0.65F, 0.65F, 3.4F, 0.65F)
                .texOffs(u + 34, v).addBox(0.15F, 15.0F, -0.65F, 0.65F, 3.0F, 0.65F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(TheHollowEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float walk = Math.min(limbSwingAmount, 1.0F);
        boolean hunting = entity.isHunting();

        this.head.xRot = headPitch * Mth.DEG_TO_RAD + (hunting ? 0.08F : -0.035F);
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.zRot = Mth.sin(ageInTicks * 0.055F) * 0.035F;

        // It stoops almost imperceptibly while watching, then folds forward into the chase.
        this.torso.xRot = (hunting ? 0.42F : 0.10F)
                + Mth.cos(limbSwing * 0.5F) * 0.035F * walk;
        this.torso.zRot = Mth.sin(ageInTicks * 0.045F) * 0.018F;

        this.rightArm.xRot = -0.04F + Mth.cos(limbSwing * 0.55F) * (hunting ? 1.05F : 0.42F) * walk;
        this.leftArm.xRot = -0.04F + Mth.cos(limbSwing * 0.55F + Mth.PI) * (hunting ? 1.05F : 0.42F) * walk;
        this.rightArm.zRot = -0.08F;
        this.leftArm.zRot = 0.08F;

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.55F) * 0.9F * walk;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.55F + Mth.PI) * 0.9F * walk;
        this.shroud.xRot = 0.04F + Mth.cos(ageInTicks * 0.08F) * 0.025F;
    }
}
