package com.astrolabs.etherealmind.client.renderer;

import com.astrolabs.etherealmind.common.entity.CosmoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CosmoSpeechBubbleRenderer {
    private static final int BUBBLE_COLOR = 0xE0FFFFFF; // Semi-transparent white
    private static final int BUBBLE_BORDER = 0xFF8B00FF; // Purple border
    private static final int TEXT_COLOR = 0xFF000000; // Black text
    private static final float BUBBLE_SCALE = 0.02f;
    private static final int DISPLAY_TIME = 100; // 5 seconds
    private static final float FADE_IN_TIME = 10f; // ticks
    private static final float FADE_OUT_TIME = 20f; // ticks
    private static final int CHARS_PER_TICK = 2; // typing speed
    
    private String currentMessage = "";
    private String currentEmote = "";
    private int displayTimer = 0;
    private int animationTimer = 0;
    private float lastScale = 0.0f;
    
    public void showMessage(String message) {
        this.currentMessage = message;
        this.displayTimer = DISPLAY_TIME;
        this.animationTimer = 0;
    }
    
    public void showEmote(String emote) {
        this.currentEmote = emote;
        this.displayTimer = DISPLAY_TIME;
        this.animationTimer = 0;
    }
    
    public void tick() {
        if (displayTimer > 0) {
            displayTimer--;
            animationTimer++;
        }
    }
    
    public void render(CosmoEntity cosmo, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (displayTimer <= 0 || (currentMessage.isEmpty() && currentEmote.isEmpty())) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        
        poseStack.pushPose();
        
        // Calculate animation values
        float fadeProgress = 1.0f;
        float scaleProgress = 1.0f;
        float typingProgress = 1.0f;
        
        // Fade in
        if (animationTimer < FADE_IN_TIME) {
            fadeProgress = animationTimer / FADE_IN_TIME;
            scaleProgress = 0.8f + (0.2f * fadeProgress);
        }
        // Fade out
        else if (displayTimer < FADE_OUT_TIME) {
            fadeProgress = displayTimer / FADE_OUT_TIME;
            scaleProgress = 1.0f - (0.2f * (1.0f - fadeProgress));
        }
        
        // Typing animation
        int maxChars = (animationTimer - (int)FADE_IN_TIME) * CHARS_PER_TICK;
        typingProgress = Math.min(1.0f, (float)maxChars / currentMessage.length());
        
        // Smooth scale transitions
        float targetScale = BUBBLE_SCALE * scaleProgress;
        lastScale = lastScale + (targetScale - lastScale) * 0.3f;
        
        // Position above COSMO with bounce
        float bounce = (float)Math.sin(animationTimer * 0.1) * 0.02f;
        poseStack.translate(0, cosmo.getBbHeight() + 0.5 + bounce, 0);
        
        // Face the camera
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-lastScale, -lastScale, lastScale);
        
        float alpha = fadeProgress;
        
        if (!currentMessage.isEmpty()) {
            // Only show typed characters
            int charsToShow = Math.max(0, Math.min(maxChars, currentMessage.length()));
            String displayMessage = currentMessage.substring(0, charsToShow);
            if (!displayMessage.isEmpty()) {
                renderSpeechBubble(poseStack, buffer, font, displayMessage, alpha);
            }
        }
        
        if (!currentEmote.isEmpty()) {
            renderEmote(poseStack, buffer, font, currentEmote, alpha);
        }
        
        poseStack.popPose();
    }
    
    private void renderSpeechBubble(PoseStack poseStack, MultiBufferSource buffer, Font font, String message, float alpha) {
        int messageWidth = font.width(message);
        int bubbleWidth = messageWidth + 10;
        int bubbleHeight = 20;
        
        // Draw bubble background
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();
        
        // Main bubble
        float x1 = -bubbleWidth / 2f;
        float x2 = bubbleWidth / 2f;
        float y1 = -bubbleHeight / 2f;
        float y2 = bubbleHeight / 2f;
        
        // Background
        int bgColor = (int)(alpha * 224) << 24 | 0xFFFFFF;
        renderQuad(vertexConsumer, matrix, x1, y1, x2, y2, bgColor);
        
        // Border
        int borderColor = (int)(alpha * 255) << 24 | 0x8B00FF;
        float borderThickness = 1.5f;
        // Top border
        renderQuad(vertexConsumer, matrix, x1 - borderThickness, y2, x2 + borderThickness, y2 + borderThickness, borderColor);
        // Bottom border
        renderQuad(vertexConsumer, matrix, x1 - borderThickness, y1 - borderThickness, x2 + borderThickness, y1, borderColor);
        // Left border
        renderQuad(vertexConsumer, matrix, x1 - borderThickness, y1, x1, y2, borderColor);
        // Right border
        renderQuad(vertexConsumer, matrix, x2, y1, x2 + borderThickness, y2, borderColor);
        
        // Draw text
        poseStack.pushPose();
        poseStack.translate(0, 0, -0.01f); // Slightly in front of bubble
        
        Component text = Component.literal(message);
        int textAlpha = (int)(alpha * 255);
        int textColor = textAlpha << 24 | 0x000000;
        
        font.drawInBatch(text, 
            -messageWidth / 2f, 
            -font.lineHeight / 2f, 
            textColor,
            false,
            matrix,
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880);
            
        poseStack.popPose();
    }
    
    private void renderEmote(PoseStack poseStack, MultiBufferSource buffer, Font font, String emote, float alpha) {
        // Render emote slightly above speech bubble
        poseStack.pushPose();
        poseStack.translate(0, 30, 0);
        poseStack.scale(2.0f, 2.0f, 2.0f); // Larger for emotes
        
        Component emoteText = Component.literal(emote);
        Matrix4f matrix = poseStack.last().pose();
        
        font.drawInBatch(emoteText,
            -font.width(emote) / 2f,
            -font.lineHeight / 2f,
            0xFFFFFFFF,
            false,
            matrix,
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880);
            
        poseStack.popPose();
    }
    
    private void renderQuad(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        float z = 0.0f;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        
        consumer.vertex(matrix, x1, y1, z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, x1, y2, z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, x2, y2, z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
        consumer.vertex(matrix, x2, y1, z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 0, 1).endVertex();
    }
}