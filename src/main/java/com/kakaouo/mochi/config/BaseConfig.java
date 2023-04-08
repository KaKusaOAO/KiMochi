package com.kakaouo.mochi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.kakaouo.mochi.utils.Logger;
import com.kakaouo.mochi.utils.Utils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class BaseConfig<T extends IBaseConfigData> {
    private final Class<T> clz;
    private final File file;
    private T data;

    public int getSkipLineCount() {
        return 0;
    }

    public final T getData() {
        return data;
    }

    protected BaseConfig(String path, Class<T> clz) {
        this.clz = clz;
        this.file = new File(Utils.getRootDirectory(), path);
        this.data = resolveDefault();
    }

    protected abstract T resolveDefault();

    protected void prepareConfigPath() {
        if (!file.exists()) {
            // noinspection RedundantSuppression (wtf)
            try {
                // noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                //
            }
        }
    }

    public void load() {
        prepareConfigPath();

        try {
            var reader = new FileReader(file);

            var lines = 0;
            var skipLines = getSkipLineCount();
            while (true) {
                if (lines >= skipLines) break;

                var r = reader.read();
                if ((char) r == '\n') {
                    lines++;
                }
            }

            data = new ObjectMapper().readValue(reader, clz);
        } catch (MismatchedInputException ex) {
            data = resolveDefault();
        } catch (Throwable ex) {
            Logger.error("An error occurred while reading config!");
            Logger.error(ex);
            data = resolveDefault();
        }

        save();
    }

    public void save() {
        try {
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

