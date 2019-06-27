# PwingItemReplacer
A small, lightweight API that handles replacement of items. 
Can be used if there is an item in distribution on your server that has got out of hand and you want removed, or if you just
want to clean up some items that may have custom lores.

This API also has support for item replacement from configs. You can check data such as if the item lore is exactly the same, or if
you want to replace items where the lore just contains a certain text. If you want to use this in your plugin, you will have to download
it and shade it in yourself. As of now, there is not a maven server set up for it, so you just have to manually copy the jar in.

Here are the available methods:
```java
    public void replaceItems(Player player, ItemStack toReplace, ItemStack replacement, boolean exactName, boolean exactLore) 
    
    public void replaceItems(String path, FileConfiguration config, Player player)
    
    public boolean isReplaceable(ItemStack item, String path, FileConfiguration config, boolean exactName, boolean exactLore, String section)
    
    public boolean isReplaceable(ItemStack item, ItemStack toReplace, boolean exactName, boolean exactLore)
```
Here is an example usage:

```yml
items:
  testitem1:
    to-replace:
      exact-name: true
      exact-lore: false
      name: "&aTest Item"
      type: stone
      lore:
      - "&aThis is an exact lore"
      - "&cwith two lines and colors"
    replacement:
      type: bricks
      name: "&aTest Item"
      amount: 2
      lore:
      - "&bBricks with a custom"
      - "&elore which is &o&b&l&mugly&ee!"
      attributes:
      - generic_attack_speed hand 1
```

In this instance, an item that has the exact name `&aTest Item` and a lore that contains the specified lines will be replaced. 

`to-replace` is the item you want to replace.

`replacement` is the item you want to replace it with.

