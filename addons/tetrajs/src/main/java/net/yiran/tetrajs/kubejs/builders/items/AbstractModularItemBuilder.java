package net.yiran.tetrajs.kubejs.builders.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractModularItemBuilder<T extends ItemBuilder> extends ItemBuilder {
    public boolean canHone = true;
    public int honeBase = 450;
    public int honeIntegrityMultiplier = 200;
    public String[] synergiesPath = new String[0];
    public JsonObject model;

    public AbstractModularItemBuilder(ResourceLocation itemID) {
        super(itemID);
        this.genModel();
    }

    @SuppressWarnings("unchecked")
    public T tjs$setSynergiesPath(String... synergiesPath) {
        this.synergiesPath = synergiesPath;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T tjs$isCanHone(boolean canHone) {
        this.canHone = canHone;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T tjs$setHoneBase(int honeBase) {
        this.honeBase = honeBase;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T tjs$setHoneIntegrityMultiplier(int honeIntegrityMultiplier) {
        this.honeIntegrityMultiplier = honeIntegrityMultiplier;
        return (T) this;
    }

    @HideFromJS
    public void genModel() {
        this.model = JsonParser.parseString("""
                {
                    "parent": "item/generated",
                    "loader": "tetra:modular_loader",
                    "display": {
                        "ground": {
                            "rotation": [ 0, 0, 0 ],
                            "translation": [ 0, 2, 0 ],
                            "scale": [ 0.5, 0.5, 0.5 ]
                        },
                        "head": {
                            "rotation": [ 0, 180, 0 ],
                            "translation": [ 0, 13, 7 ],
                            "scale": [ 1, 1, 1 ]
                        },
                        "fixed": {
                            "rotation": [ 0, 180, 0 ],
                            "scale": [ 1, 1, 1 ]
                        },
                        "thirdperson_righthand": {
                            "rotation": [ 0, -90, 55 ],
                            "translation": [ 0, 4.0, 0.5 ],
                            "scale": [ 0.85, 0.85, 0.85 ]
                        },
                        "thirdperson_lefthand": {
                            "rotation": [ 0, 90, -55 ],
                            "translation": [ 0, 4.0, 0.5 ],
                            "scale": [ 0.85, 0.85, 0.85 ]
                        },
                        "firstperson_righthand": {
                            "rotation": [ 0, -90, 25 ],
                            "translation": [ 1.13, 3.2, 1.13 ],
                            "scale": [ 0.68, 0.68, 0.68 ]
                        },
                        "firstperson_lefthand": {
                            "rotation": [ 0, 90, -25 ],
                            "translation": [ 1.13, 3.2, 1.13 ],
                            "scale": [ 0.68, 0.68, 0.68 ]
                        }
                    },
                    "variants": {
                        "throwing": {
                            "thirdperson_righthand": {
                                "rotation": [ 0, -90, 225 ],
                                "translation": [ 0, -4.0, 0.5 ],
                                "scale": [ 0.85, 0.85, 0.85 ]
                            },
                            "thirdperson_lefthand": {
                                "rotation": [ 0, 90, -225 ],
                                "translation": [ 0, -4.0, 0.5 ],
                                "scale": [ 0.85, 0.85, 0.85 ]
                            },
                            "firstperson_righthand": {
                                "rotation": [ 0, -90, 70 ],
                                "translation": [ 2.3, -3, -2 ],
                                "scale": [ 0.68, 0.68, 0.68 ]
                            },
                            "firstperson_lefthand": {
                                "rotation": [ 0, 90, -70 ],
                                "translation": [ 2.3, -3, -2 ],
                                "scale": [ 0.68, 0.68, 0.68 ]
                            }
                        },
                        "blocking": {
                            "thirdperson_righthand": {
                                "rotation": [ 0, -90, 55 ],
                                "translation": [ 0, 4.0, 0.5 ],
                                "scale": [ 0.85, 0.85, 0.85 ]
                            },
                            "thirdperson_lefthand": {
                                "rotation": [ 0, 90, -55 ],
                                "translation": [ 0, 4.0, 0.5 ],
                                "scale": [ 0.85, 0.85, 0.85 ]
                            },
                            "firstperson_righthand": {
                                "rotation": [ 100, -19, 150 ],
                                "translation": [ -4.13, 3.2, 1.13 ],
                                "scale": [ 0.68, 0.68, 0.68 ]
                            },
                            "firstperson_lefthand": {
                                "rotation": [ 100, 161, -150 ],
                                "translation": [ -4.13, 3.2, 1.13 ],
                                "scale": [ 0.68, 0.68, 0.68 ]
                            }
                        }
                    }
                }
                """).getAsJsonObject();
    }

    @Override
    protected void generateItemModels(KubeAssetGenerator generator) {
        generator.itemModel(this.id, model -> model.custom(json -> this.applyModel(json)));
    }

    private void applyModel(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : this.model.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }
    }
}
