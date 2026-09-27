package hotel.animation;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


// =========================================================
// WEATHER ANIMATION
// =========================================================

public class WeatherAnimation {


    // =========================================================
    // MAIN METHOD
    // =========================================================

    public static StackPane createAnimation(
            String condition) {

        StackPane container =
                new StackPane();


        // =====================================================
        // ANIMATION AREA
        // =====================================================

        container.setPrefSize(
                120,
                65
        );

        container.setMinSize(
                120,
                65
        );

        container.setMaxSize(
                120,
                65
        );


        // =====================================================
        // CHECK WEATHER CONDITION
        // =====================================================

        String weatherCondition =
                condition == null
                        ? ""
                        : condition.toLowerCase();


        // =====================================================
        // CLEAR / SUNNY
        // =====================================================

        if (weatherCondition.contains("clear")) {

            container.getChildren().add(
                    createSunAnimation()
            );

            return container;
        }


        // =====================================================
        // RAIN / DRIZZLE
        // =====================================================

        if (weatherCondition.contains("rain")
                || weatherCondition.contains("drizzle")) {

            container.getChildren().add(
                    createRainAnimation()
            );

            return container;
        }


        // =====================================================
        // THUNDERSTORM
        // =====================================================

        if (weatherCondition.contains("thunderstorm")) {

            container.getChildren().add(
                    createThunderstormAnimation()
            );

            return container;
        }


        // =====================================================
        // SNOW
        // =====================================================

        if (weatherCondition.contains("snow")) {

            container.getChildren().add(
                    createSnowAnimation()
            );

            return container;
        }


        // =====================================================
        // FOG
        // =====================================================

        if (weatherCondition.contains("fog")) {

            container.getChildren().add(
                    createFogAnimation()
            );

            return container;
        }


        // =====================================================
        // CLOUDY
        // =====================================================

        if (weatherCondition.contains("cloud")) {

            container.getChildren().add(
                    createCloudAnimation()
            );

            return container;
        }


        // =====================================================
        // DEFAULT
        // =====================================================

        container.getChildren().add(
                createCloudAnimation()
        );

        return container;
    }


    // =========================================================
    // SUN ANIMATION
    // =========================================================

    private static Node createSunAnimation() {

        Group sunGroup =
                new Group();


        // =====================================================
        // SUN BODY
        // =====================================================

        Circle sun =
                new Circle(
                        25,
                        Color.web("#FACC15")
                );


        sun.setStroke(
                Color.web("#EAB308")
        );

        sun.setStrokeWidth(
                2
        );


        // =====================================================
        // SUN RAYS
        // =====================================================

        List<Line> rays =
                new ArrayList<>();


        double centerX = 0;
        double centerY = 0;


        for (int i = 0; i < 8; i++) {

            double angle =
                    Math.toRadians(
                            i * 45
                    );


            double startX =
                    centerX
                            + Math.cos(angle) * 32;

            double startY =
                    centerY
                            + Math.sin(angle) * 32;


            double endX =
                    centerX
                            + Math.cos(angle) * 42;

            double endY =
                    centerY
                            + Math.sin(angle) * 42;


            Line ray =
                    new Line(
                            startX,
                            startY,
                            endX,
                            endY
                    );


            ray.setStroke(
                    Color.web("#F59E0B")
            );

            ray.setStrokeWidth(
                    3
            );

            ray.setStrokeLineCap(
                    javafx.scene.shape.StrokeLineCap.ROUND
            );


            rays.add(
                    ray
            );
        }


        // =====================================================
        // ADD SUN + RAYS
        // =====================================================

        sunGroup.getChildren().add(
                sun
        );

        sunGroup.getChildren().addAll(
                rays
        );


        // =====================================================
        // SUN ROTATION
        // =====================================================

        RotateTransition rotate =
                new RotateTransition(
                        Duration.seconds(8),
                        sunGroup
                );


        rotate.setByAngle(
                360
        );

        rotate.setCycleCount(
                RotateTransition.INDEFINITE
        );

        rotate.setInterpolator(
                javafx.animation.Interpolator.LINEAR
        );

        rotate.play();


        // =====================================================
        // SUN SHINE
        // =====================================================

        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(1.5),
                        sunGroup
                );


        fade.setFromValue(
                0.75
        );

        fade.setToValue(
                1.0
        );

        fade.setAutoReverse(
                true
        );

        fade.setCycleCount(
                FadeTransition.INDEFINITE
        );

        fade.play();


        return sunGroup;
    }


    // =========================================================
    // CLOUD ANIMATION
    // =========================================================

    private static Node createCloudAnimation() {

        Group cloud =
                createCloud();


        // =====================================================
        // CLOUD SIZE
        // =====================================================

        cloud.setScaleX(
                0.72
        );

        cloud.setScaleY(
                0.72
        );


        // =====================================================
        // CLOUD POSITION
        // =====================================================

        cloud.setTranslateY(
                0
        );


        // =====================================================
        // CLOUD MOVEMENT
        // =====================================================

        TranslateTransition move =
                new TranslateTransition(
                        Duration.seconds(3),
                        cloud
                );


        move.setFromX(
                -5
        );

        move.setToX(
                5
        );

        move.setAutoReverse(
                true
        );

        move.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        move.play();


        return cloud;
    }


    // =========================================================
    // CREATE CLOUD
    // =========================================================

    private static Group createCloud() {

        Group cloud =
                new Group();


        // =====================================================
        // LEFT CLOUD
        // =====================================================

        Circle left =
                new Circle(
                        -22,
                        5,
                        15,
                        Color.web("#CBD5E1")
                );


        // =====================================================
        // MIDDLE CLOUD
        // =====================================================

        Circle middle =
                new Circle(
                        0,
                        -5,
                        21,
                        Color.web("#E2E8F0")
                );


        // =====================================================
        // RIGHT CLOUD
        // =====================================================

        Circle right =
                new Circle(
                        20,
                        6,
                        16,
                        Color.web("#CBD5E1")
                );


        // =====================================================
        // CLOUD BASE
        // =====================================================

        Rectangle base =
                new Rectangle(
                        -38,
                        5,
                        75,
                        20
                );


        base.setArcWidth(
                15
        );

        base.setArcHeight(
                15
        );

        base.setFill(
                Color.web("#CBD5E1")
        );


        // =====================================================
        // ADD CLOUD PARTS
        // =====================================================

        cloud.getChildren().addAll(
                left,
                middle,
                right,
                base
        );


        return cloud;
    }


    // =========================================================
    // RAIN ANIMATION
    // =========================================================

    private static Node createRainAnimation() {

        // IMPORTANT:
        // StackPane centers the cloud because the cloud's
        // coordinates are based around (0,0).

        StackPane rainContainer =
                new StackPane();


        // =====================================================
        // ANIMATION AREA
        // =====================================================

        rainContainer.setPrefSize(
                120,
                65
        );

        rainContainer.setMinSize(
                120,
                65
        );

        rainContainer.setMaxSize(
                120,
                65
        );


        // =====================================================
        // CLOUD
        // =====================================================

        Group cloud =
                createCloud();


        cloud.setScaleX(
                0.72
        );

        cloud.setScaleY(
                0.72
        );


        // Move cloud slightly upward
        cloud.setTranslateY(
                -8
        );


        // =====================================================
        // RAIN DROPS
        // =====================================================

        Group rainDrops =
                new Group();


        List<Line> drops =
                new ArrayList<>();


        // These positions are centered around 0.
        double[] xPositions = {

                -22,
                -11,
                0,
                11,
                22
        };


        for (double x : xPositions) {

            Line drop =
                    new Line(
                            x,
                            0,
                            x - 3,
                            12
                    );


            drop.setStroke(
                    Color.web("#3B82F6")
            );

            drop.setStrokeWidth(
                    2
            );

            drop.setStrokeLineCap(
                    javafx.scene.shape.StrokeLineCap.ROUND
            );


            drops.add(
                    drop
            );

            rainDrops.getChildren().add(
                    drop
            );
        }


        // =====================================================
        // POSITION RAIN BELOW CLOUD
        // =====================================================

        rainDrops.setTranslateY(
                17
        );


        // =====================================================
        // ADD CLOUD + RAIN
        // =====================================================

        rainContainer.getChildren().addAll(
                cloud,
                rainDrops
        );


        // =====================================================
        // RAIN FALLING ANIMATION
        // =====================================================

        ParallelTransition rainAnimation =
                new ParallelTransition();


        for (Line drop : drops) {

            TranslateTransition fall =
                    new TranslateTransition(
                            Duration.seconds(0.7),
                            drop
                    );


            fall.setFromY(
                    -3
            );

            fall.setToY(
                    18
            );

            fall.setCycleCount(
                    TranslateTransition.INDEFINITE
            );

            fall.setAutoReverse(
                    false
            );


            rainAnimation
                    .getChildren()
                    .add(
                            fall
                    );
        }


        rainAnimation.play();


        // =====================================================
        // CLOUD MOVEMENT
        // =====================================================

        TranslateTransition cloudMovement =
                new TranslateTransition(
                        Duration.seconds(3),
                        cloud
                );


        cloudMovement.setFromX(
                -4
        );

        cloudMovement.setToX(
                4
        );

        cloudMovement.setAutoReverse(
                true
        );

        cloudMovement.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        cloudMovement.play();


        return rainContainer;
    }


    // =========================================================
    // THUNDERSTORM ANIMATION
    // =========================================================

    private static Node createThunderstormAnimation() {

        StackPane container =
                new StackPane();


        container.setPrefSize(
                120,
                65
        );

        container.setMinSize(
                120,
                65
        );

        container.setMaxSize(
                120,
                65
        );


        // =====================================================
        // CLOUD
        // =====================================================

        Group cloud =
                createCloud();


        cloud.setScaleX(
                0.72
        );

        cloud.setScaleY(
                0.72
        );

        cloud.setTranslateY(
                -7
        );


        // =====================================================
        // LIGHTNING
        // =====================================================

        Polygon lightning =
                new Polygon(

                        5.0, 0.0,

                        -5.0, 17.0,

                        2.0, 17.0,

                        -7.0, 35.0,

                        12.0, 13.0,

                        5.0, 13.0
                );


        lightning.setFill(
                Color.web("#FACC15")
        );

        lightning.setStroke(
                Color.web("#EAB308")
        );

        lightning.setStrokeWidth(
                1
        );

        lightning.setTranslateY(
                8
        );


        // =====================================================
        // ADD CLOUD + LIGHTNING
        // =====================================================

        container.getChildren().addAll(
                cloud,
                lightning
        );


        // =====================================================
        // LIGHTNING FLASH
        // =====================================================

        FadeTransition flash =
                new FadeTransition(
                        Duration.seconds(1.2),
                        lightning
                );


        flash.setFromValue(
                0.0
        );

        flash.setToValue(
                1.0
        );

        flash.setAutoReverse(
                true
        );

        flash.setCycleCount(
                FadeTransition.INDEFINITE
        );

        flash.play();


        // =====================================================
        // CLOUD MOVEMENT
        // =====================================================

        TranslateTransition move =
                new TranslateTransition(
                        Duration.seconds(3),
                        cloud
                );


        move.setFromX(
                -4
        );

        move.setToX(
                4
        );

        move.setAutoReverse(
                true
        );

        move.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        move.play();


        return container;
    }


    // =========================================================
    // SNOW ANIMATION
    // =========================================================

    private static Node createSnowAnimation() {

        // Use StackPane here as well so the snow cloud is centered.

        StackPane snowContainer =
                new StackPane();


        snowContainer.setPrefSize(
                120,
                65
        );

        snowContainer.setMinSize(
                120,
                65
        );

        snowContainer.setMaxSize(
                120,
                65
        );


        // =====================================================
        // CLOUD
        // =====================================================

        Group cloud =
                createCloud();


        cloud.setScaleX(
                0.72
        );

        cloud.setScaleY(
                0.72
        );

        cloud.setTranslateY(
                -8
        );


        // =====================================================
        // SNOWFLAKES
        // =====================================================

        Group snowflakesGroup =
                new Group();


        List<Circle> snowflakes =
                new ArrayList<>();


        double[] xPositions = {

                -22,
                -11,
                0,
                11,
                22
        };


        for (double x : xPositions) {

            Circle snowflake =
                    new Circle(
                            x,
                            0,
                            3,
                            Color.WHITE
                    );


            snowflake.setStroke(
                    Color.web("#93C5FD")
            );

            snowflake.setStrokeWidth(
                    1
            );


            snowflakes.add(
                    snowflake
            );

            snowflakesGroup
                    .getChildren()
                    .add(
                            snowflake
                    );
        }


        // =====================================================
        // POSITION SNOW BELOW CLOUD
        // =====================================================

        snowflakesGroup.setTranslateY(
                17
        );


        // =====================================================
        // ADD CLOUD + SNOW
        // =====================================================

        snowContainer.getChildren().addAll(
                cloud,
                snowflakesGroup
        );


        // =====================================================
        // SNOW FALLING ANIMATION
        // =====================================================

        ParallelTransition snowAnimation =
                new ParallelTransition();


        for (Circle snowflake : snowflakes) {

            TranslateTransition fall =
                    new TranslateTransition(
                            Duration.seconds(1.8),
                            snowflake
                    );


            fall.setFromY(
                    -4
            );

            fall.setToY(
                    20
            );

            fall.setCycleCount(
                    TranslateTransition.INDEFINITE
            );

            fall.setAutoReverse(
                    false
            );


            snowAnimation
                    .getChildren()
                    .add(
                            fall
                    );
        }


        snowAnimation.play();


        // =====================================================
        // CLOUD MOVEMENT
        // =====================================================

        TranslateTransition cloudMovement =
                new TranslateTransition(
                        Duration.seconds(3),
                        cloud
                );


        cloudMovement.setFromX(
                -3
        );

        cloudMovement.setToX(
                3
        );

        cloudMovement.setAutoReverse(
                true
        );

        cloudMovement.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        cloudMovement.play();


        return snowContainer;
    }


    // =========================================================
    // FOG ANIMATION
    // =========================================================

    private static Node createFogAnimation() {

        VBoxLikeFog fog =
                new VBoxLikeFog();


        TranslateTransition move =
                new TranslateTransition(
                        Duration.seconds(3),
                        fog
                );


        move.setFromX(
                -10
        );

        move.setToX(
                10
        );

        move.setAutoReverse(
                true
        );

        move.setCycleCount(
                TranslateTransition.INDEFINITE
        );

        move.play();


        return fog;
    }


    // =========================================================
    // SMALL FOG CLASS
    // =========================================================

    private static class VBoxLikeFog
            extends Group {


        VBoxLikeFog() {

            Line line1 =
                    createFogLine(
                            -35,
                            0,
                            35,
                            0
                    );


            Line line2 =
                    createFogLine(
                            -25,
                            12,
                            40,
                            12
                    );


            Line line3 =
                    createFogLine(
                            -40,
                            24,
                            25,
                            24
                    );


            getChildren().addAll(
                    line1,
                    line2,
                    line3
            );
        }


        // =====================================================
        // CREATE FOG LINE
        // =====================================================

        private Line createFogLine(
                double startX,
                double startY,
                double endX,
                double endY) {

            Line line =
                    new Line(
                            startX,
                            startY,
                            endX,
                            endY
                    );


            line.setStroke(
                    Color.web("#94A3B8")
            );

            line.setStrokeWidth(
                    5
            );

            line.setOpacity(
                    0.65
            );

            line.setStrokeLineCap(
                    javafx.scene.shape.StrokeLineCap.ROUND
            );


            return line;
        }
    }
}