package chiquita.mineclient.settings;

import chiquita.mineclient.module.Module;

public class Value<T>
{

    private String name;
    private String[] alias;
    private T[] modes;
    private String desc;
    private Module mod;
    public ValueListeners listener;

    private T value;

    private T min;
    private T max;
    private T inc;
    private int decimals;

    public Value(String name, String[] alias, String desc)
    {
        this.name = name;
        this.alias = alias;
        this.desc = desc;
    }

    public Value(String name, String[] alias, String desc, T value)
    {
        this(name, alias, desc);
        this.value = value;
    }

    public Value(String name, String[] alias, String desc, T value, T min, T max, T inc, int decimals)
    {
        this(name, alias, desc, value);
        this.min = min;
        this.max = max;
        this.inc = inc;
        this.decimals = decimals;
    }

    public Value(String name, String[] alias, String desc, T[] modes) {
        this(name, alias, desc);
        this.modes = modes;
    }

    public <T> T clamp(T value, T min, T max)
    {
        return ((Comparable) value).compareTo(min) < 0 ? min : (((Comparable) value).compareTo(max) > 0 ? max : value);
    }

    public T getValue()
    {
        return this.value;
    }

    public int getDecimals() {
        return this.decimals;
    }

    public void setValue(T value)
    {
        if (min != null && max != null) {
            final Number val = (Number) value;
            final Number min = (Number) this.min;
            final Number max = (Number) this.max;
            this.value = (T) this.clamp(val, min, max);
        }
        else {
            this.value = value;
        }

        if (mod != null)
            mod.signalValueChange(this);
        if (listener != null)
            listener.onValueChange(this);
    }

    public String getNextEnumValue(boolean reverse)
    {
        final Enum currentEnum = (Enum) this.getValue();

        int i = 0;

        for (; i < this.value.getClass().getEnumConstants().length; i++)
        {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(currentEnum.name()))
            {
                break;
            }
        }

        return this.value.getClass()
                .getEnumConstants()[(reverse ? (i != 0 ? i - 1 : value.getClass().getEnumConstants().length - 1)
                : i + 1) % value.getClass().getEnumConstants().length].toString();
    }

    public int getEnum(String input)
    {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; i++)
        {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input))
            {
                return i;
            }
        }
        return -1;
    }

    public Enum getEnumReal(String input)
    {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; i++)
        {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input))
            {
                return e;
            }
        }
        return null;
    }

    public void setEnumValue(String value)
    {
        for (Enum e : ((Enum) this.value).getClass().getEnumConstants())
        {
            if (e.name().equalsIgnoreCase(value))
            {
                setValue((T)e);
                break;
            }
        }

        if (mod != null)
            mod.signalEnumChange();
    }

    public T getMin()
    {
        return min;
    }

    public void setMin(T min)
    {
        this.min = min;
    }

    public T getMax()
    {
        return max;
    }

    public void setMax(T max)
    {
        this.max = max;
    }

    public T getInc()
    {
        return inc;
    }

    public void setInc(T inc)
    {
        this.inc = inc;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String[] getAlias()
    {
        return alias;
    }

    public void setAlias(String[] alias)
    {
        this.alias = alias;
    }

    public T[] getList() {
        return modes;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public void SetListener(ValueListeners valueListeners)
    {
        listener = valueListeners;
    }

    public void initializeMod(Module mod)
    {
        this.mod = mod;
    }

    public void setForcedValue(T value)
    {
        if (min != null && max != null)
        {
            final Number val = (Number) value;
            final Number min = (Number) this.min;
            final Number max = (Number) this.max;
            this.value = (T) val;
            // this.value = (T) this.clamp(val, min, max);
        }
        else
        {
            this.value = value;
        }
    }
}
